<template>
  <div class="assistant-root">
    <!-- 折叠状态：小型可拖拽卡通图标 -->
    <div
      v-if="!expanded"
      class="collapsed-avatar"
      :style="{ top: avatarTop + 'px' }"
      @mousedown="startDrag"
      @click="handleAvatarClick"
    >
      <div class="avatar-bubble">
        <div class="avatar-face">
          <div class="avatar-eyes">
            <span class="eye left"></span>
            <span class="eye right"></span>
          </div>
          <div class="avatar-mouth"></div>
        </div>
        <div class="avatar-antenna">
          <span class="antenna-ball"></span>
        </div>
      </div>
      <div class="avatar-label">AI助手</div>
    </div>

    <!-- 展开状态：聊天面板 -->
    <div v-else class="chat-panel">
      <div class="panel-header">
        <div class="header-left">
          <span class="header-avatar">🤖</span>
          <span class="header-title">AI助手</span>
        </div>
        <el-button class="collapse-btn" @click="collapse" circle size="small">
          <span>—</span>
        </el-button>
      </div>

      <div class="chat-messages" ref="messagesRef">
        <el-timeline style="max-width: 100%">
          <el-timeline-item
            v-for="(activity, index) in activities"
            :key="index"
            :icon="activity.icon"
            :type="activity.type"
            :color="activity.color"
            :size="activity.size"
            :hollow="activity.hollow"
            :timestamp="activity.timestamp"
          >
            <div class="msg-content" v-html="formatContent(activity.content, index)" @click="handleContentClick"></div>
          </el-timeline-item>
        </el-timeline>
      </div>

      <div class="chat-input-area">
        <el-input
          v-model="msg"
          type="textarea"
          :rows="2"
          :autosize="{ minRows: 2, maxRows: 5 }"
          placeholder="在这里输入消息"
          @keydown.enter="sendMsg();"
        />
        <el-button @click="sendMsg()">发送</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { MoreFilled } from '@element-plus/icons-vue'
import { ref, nextTick, watch, onBeforeUnmount } from "vue";
import { v4 as uuidv4 } from "uuid";
import request from "../utils/request.js";

const chatId = uuidv4();

/**
 * 根据用户消息内容智能路由到对应服务的AI助手
 * 优先级：flight > addr > manager（默认）
 */
const SERVICE_RULES = [
  {
    service: 'flight',
    keywords: [
      '航班', '机票', '预订', '订票', '退票', '改签', '舱位',
      '出发', '到达', '登机', '乘机',
      '客机', '飞机', '机型', '座位', '票价',
      '预订号', '客户航班', '航班信息', '航班状态',
      ' booking', 'flight', 'plane'
    ]
  },
  {
    service: 'order',
    keywords: [
      '订单', '优惠券', '折扣', '支付', '取消订单',
      '乘客', '旅客', '乘客信息', '乘客列表', '订单详情', '订单列表',
      '满减', '折扣券', '优惠券码', '订单编码',
      'order', 'coupon', 'passenger'
    ]
  },
  {
    service: 'addr',
    keywords: [
      '城市', '地址', '机场', '运营地址', '开通城市',
      '城市编码', '城市名称', '运营城市', 'addr'
    ]
  }
];

const routeService = (message, defaultService) => {
  const msg = message.toLowerCase();
  for (const rule of SERVICE_RULES) {
    if (rule.keywords.some(kw => msg.includes(kw.toLowerCase()))) {
      return rule.service;
    }
  }
  return defaultService || 'manager';
};

const escapeHtml = (str) => {
  return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
};

export default {
  name: 'Assistant',
  emits: ['booking-updated', 'expand-change'],
  props: {
    servicePrefix: {
      type: String,
      default: 'flight'
    }
  },
  setup(props, { emit }) {
    const expanded = ref(false);
    const messagesRef = ref(null);
    const avatarTop = ref(200);
    const isDragging = ref(false);
    const dragStartY = ref(0);
    const dragStartTop = ref(0);
    const hasDragged = ref(false);

    const activities = ref([
      {
        content: '⭐欢迎来到吉利航空✈！请问有什么可以帮您的?',
        timestamp: new Date().toLocaleDateString() + " " + new Date().toLocaleTimeString(),
        color: '#0bbd87',
      },
    ]);
    const msg = ref('');
    let count = 2;

    const FOLD_THRESHOLD = 10;
    const expandedTables = ref(new Set());

    const renderTable = (lines, tableKey, isExpanded) => {
      if (lines.length === 0) return '';
      const parseRow = (line) => {
        return line.split('|').filter((_, i, arr) => i > 0 && i < arr.length - 1).map(cell => cell.trim());
      };
      const headers = parseRow(lines[0]);
      const rows = lines.slice(1).map(parseRow);
      const shouldFold = rows.length > FOLD_THRESHOLD;
      const displayRows = shouldFold && !isExpanded ? rows.slice(0, FOLD_THRESHOLD) : rows;
      const hiddenCount = rows.length - FOLD_THRESHOLD;

      let html = '<table class="msg-table"><thead><tr>';
      headers.forEach(h => { html += `<th>${escapeHtml(h)}</th>`; });
      html += '</tr></thead><tbody>';
      displayRows.forEach(row => {
        html += '<tr>';
        row.forEach(cell => { html += `<td>${escapeHtml(cell)}</td>`; });
        html += '</tr>';
      });
      html += '</tbody></table>';

      if (shouldFold) {
        if (isExpanded) {
          html += `<div class="table-toggle" data-table-key="${tableKey}">收起 ▲</div>`;
        } else {
          html += `<div class="table-toggle" data-table-key="${tableKey}">展开更多（还有 ${hiddenCount} 条）▼</div>`;
        }
      }
      return html;
    };

    const formatContent = (content, activityIndex) => {
      if (!content) return '';
      const lines = content.split('\n');
      const tableLines = [];
      const otherLines = [];
      let inTable = false;

      for (const line of lines) {
        const trimmed = line.trim();
        if (trimmed.startsWith('|') && trimmed.endsWith('|')) {
          if (/^\|[\s\-:|]+\|$/.test(trimmed)) continue;
          inTable = true;
          tableLines.push(trimmed);
        } else {
          if (inTable) {
            otherLines.push({ type: 'table', lines: [...tableLines] });
            tableLines.length = 0;
            inTable = false;
          }
          otherLines.push({ type: 'text', value: line });
        }
      }
      if (tableLines.length > 0) {
        otherLines.push({ type: 'table', lines: [...tableLines] });
      }

      let html = '';
      let tableCounter = 0;
      for (const block of otherLines) {
        if (block.type === 'text') {
          if (block.value.trim() !== '') {
            html += `<p>${escapeHtml(block.value)}</p>`;
          }
        } else {
          const tableKey = `${activityIndex}-${tableCounter}`;
          const isExpanded = expandedTables.value.has(tableKey);
          html += renderTable(block.lines, tableKey, isExpanded);
          tableCounter++;
        }
      }
      return html;
    };

    const handleContentClick = (e) => {
      const target = e.target.closest('.table-toggle');
      if (!target) return;
      const key = target.dataset.tableKey;
      const newSet = new Set(expandedTables.value);
      if (newSet.has(key)) {
        newSet.delete(key);
      } else {
        newSet.add(key);
      }
      expandedTables.value = newSet;
    };

    const startDrag = (e) => {
      isDragging.value = true;
      hasDragged.value = false;
      dragStartY.value = e.clientY;
      dragStartTop.value = avatarTop.value;
      document.addEventListener('mousemove', onDrag);
      document.addEventListener('mouseup', stopDrag);
      e.preventDefault();
    };

    const onDrag = (e) => {
      if (!isDragging.value) return;
      const deltaY = e.clientY - dragStartY.value;
      if (Math.abs(deltaY) > 3) hasDragged.value = true;
      const maxTop = window.innerHeight - 80;
      avatarTop.value = Math.max(20, Math.min(maxTop, dragStartTop.value + deltaY));
    };

    const stopDrag = () => {
      isDragging.value = false;
      document.removeEventListener('mousemove', onDrag);
      document.removeEventListener('mouseup', stopDrag);
    };

    const handleAvatarClick = () => {
      if (hasDragged.value) return;
      expanded.value = true;
      emit('expand-change', true);
    };

    const collapse = () => {
      expanded.value = false;
      emit('expand-change', false);
    };

    onBeforeUnmount(() => {
      document.removeEventListener('mousemove', onDrag);
      document.removeEventListener('mouseup', stopDrag);
    });

    const scrollToBottom = () => {
      nextTick(() => {
        if (messagesRef.value) {
          messagesRef.value.scrollTop = messagesRef.value.scrollHeight;
        }
      });
    };

    watch(() => activities.value.length, scrollToBottom);

    const sendMsg = async () => {
      if (!msg.value || msg.value.trim() === '') return;

      activities.value.push({
        content: `你:${msg.value}`,
        timestamp: new Date().toLocaleDateString() + " " + new Date().toLocaleTimeString(),
        size: 'large',
        type: 'primary',
        icon: MoreFilled,
      });

      activities.value.push({
        content: 'waiting...',
        timestamp: new Date().toLocaleDateString() + " " + new Date().toLocaleTimeString(),
        color: '#0bbd87',
      });

      scrollToBottom();

      const service = routeService(msg.value, props.servicePrefix);
      const apiPaths = {
        flight: '/flight/assistant',
        order: '/order/assistant',
        addr: '/addr/assistant',
        manager: '/manager/assistant'
      };
      const basePath = apiPaths[service] || apiPaths.manager;
      const messageValue = msg.value;
      msg.value = '';

      try {
        const res = await request.get(`${basePath}/chat`, {
          params: { chatId, message: messageValue }
        });
        activities.value[count].content = res.data || '';
        count = count + 2;
        scrollToBottom();
        emit('booking-updated');
      } catch (err) {
        console.error('AI助手请求失败:', err);
        activities.value[count].content = '❌ 连接失败，请稍后重试';
        activities.value[count].color = '#ff4d4f';
      }
    };

    return {
      expanded,
      messagesRef,
      avatarTop,
      activities,
      msg,
      formatContent,
      handleContentClick,
      startDrag,
      handleAvatarClick,
      collapse,
      sendMsg,
    };
  },
};
</script>

<style scoped>
.assistant-root {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* ========== 折叠状态 - 小型可拖拽卡通角色 ========== */
.collapsed-avatar {
  position: fixed;
  right: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: grab;
  user-select: none;
  z-index: 100;
  transition: opacity 0.2s;
}

.collapsed-avatar:active {
  cursor: grabbing;
}

.avatar-bubble {
  position: relative;
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.5), 0 0 30px rgba(118, 75, 162, 0.2);
  animation: breathe 3s ease-in-out infinite;
  transition: transform 0.2s, box-shadow 0.2s;
}

.collapsed-avatar:hover .avatar-bubble {
  animation: none;
  transform: scale(1.15);
  box-shadow: 0 6px 24px rgba(102, 126, 234, 0.7), 0 0 40px rgba(118, 75, 162, 0.3);
}

@keyframes breathe {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.06); }
}

.avatar-face {
  position: relative;
  width: 36px;
  height: 30px;
}

.avatar-eyes {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 4px;
}

.eye {
  width: 7px;
  height: 8px;
  background: #fff;
  border-radius: 50%;
  display: block;
  position: relative;
  animation: blink 4s ease-in-out infinite;
}

.eye::after {
  content: '';
  position: absolute;
  width: 3.5px;
  height: 3.5px;
  background: #1a1a2e;
  border-radius: 50%;
  top: 2.5px;
  left: 2px;
}

@keyframes blink {
  0%, 90%, 100% { transform: scaleY(1); }
  95% { transform: scaleY(0.1); }
}

.avatar-mouth {
  width: 12px;
  height: 6px;
  border-bottom: 2px solid #fff;
  border-radius: 0 0 50% 50%;
  margin: 3px auto 0;
}

.avatar-antenna {
  position: absolute;
  top: -2px;
  left: 50%;
  transform: translateX(-50%);
  width: 2px;
  height: 10px;
  background: rgba(255, 255, 255, 0.6);
}

.antenna-ball {
  position: absolute;
  top: -5px;
  left: -3px;
  width: 8px;
  height: 8px;
  background: #00ffcc;
  border-radius: 50%;
  box-shadow: 0 0 8px #00ffcc, 0 0 16px rgba(0, 255, 204, 0.5);
  animation: glow 2s ease-in-out infinite;
}

@keyframes glow {
  0%, 100% { box-shadow: 0 0 8px #00ffcc, 0 0 16px rgba(0, 255, 204, 0.5); }
  50% { box-shadow: 0 0 12px #00ffcc, 0 0 24px rgba(0, 255, 204, 0.8); }
}

.avatar-label {
  margin-top: 10px;
  background: linear-gradient(135deg, #00d4ff, #00ffcc);
  color: #1a1a2e;
  font-size: 10px;
  font-weight: 700;
  padding: 1px 8px;
  border-radius: 8px;
  white-space: nowrap;
  letter-spacing: 1px;
}

/* ========== 展开状态 - 聊天面板 ========== */
.chat-panel {
  height: 100%;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.05);
  display: flex;
  flex-direction: column;
  animation: expandIn 0.3s ease-out;
}

@keyframes expandIn {
  from { opacity: 0; transform: translateX(20px); }
  to { opacity: 1; transform: translateX(0); }
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 14px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.15), rgba(118, 75, 162, 0.1));
  border-bottom: 1px solid rgba(0, 212, 255, 0.1);
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-avatar {
  font-size: 20px;
}

.header-title {
  font-size: 14px;
  font-weight: 600;
  background: linear-gradient(135deg, #00d4ff, #00ffcc);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: 2px;
}

.collapse-btn {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(0, 212, 255, 0.2) !important;
  color: #00d4ff !important;
  font-size: 14px;
  width: 26px !important;
  height: 26px !important;
}

.collapse-btn:hover {
  background: rgba(0, 212, 255, 0.15) !important;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
  background: rgba(255, 255, 255, 0.02);
}

.chat-messages::-webkit-scrollbar {
  width: 4px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: rgba(0, 212, 255, 0.3);
  border-radius: 2px;
}

.chat-input-area {
  padding: 0;
  display: flex;
  align-items: flex-end;
  flex-shrink: 0;
}

.chat-input-area .el-textarea {
  flex: 1;
}

.chat-input-area button {
  margin: 4px 8px 4px 0;
  flex-shrink: 0;
}

:deep(.el-timeline-item__content) {
  color: rgba(255, 255, 255, 0.85);
}

:deep(.el-timeline-item__timestamp) {
  color: rgba(255, 255, 255, 0.4);
}

:deep(.el-input__inner) {
  color: #000000;
}

.msg-content {
  white-space: pre-wrap;
  word-break: break-word;
  font-size: 13px;
  line-height: 1.6;
}

.msg-content p {
  margin: 4px 0;
}

.msg-content :deep(.msg-table) {
  width: 100%;
  border-collapse: collapse;
  margin: 8px 0;
  font-size: 12px;
}

.msg-content :deep(.msg-table th),
.msg-content :deep(.msg-table td) {
  border: 1px solid rgba(0, 212, 255, 0.25);
  padding: 6px 8px;
  text-align: left;
  white-space: nowrap;
}

.msg-content :deep(.msg-table th) {
  background: rgba(0, 212, 255, 0.1);
  color: #00d4ff;
  font-weight: 600;
}

.msg-content :deep(.msg-table td) {
  color: rgba(255, 255, 255, 0.8);
}

.msg-content :deep(.msg-table tr:hover td) {
  background: rgba(0, 212, 255, 0.05);
}

.msg-content :deep(.table-toggle) {
  display: inline-block;
  margin-top: 6px;
  padding: 4px 14px;
  font-size: 12px;
  color: #00d4ff;
  background: rgba(0, 212, 255, 0.08);
  border: 1px solid rgba(0, 212, 255, 0.25);
  border-radius: 4px;
  cursor: pointer;
  user-select: none;
  transition: background 0.2s, border-color 0.2s;
}

.msg-content :deep(.table-toggle:hover) {
  background: rgba(0, 212, 255, 0.18);
  border-color: rgba(0, 212, 255, 0.5);
}
</style>