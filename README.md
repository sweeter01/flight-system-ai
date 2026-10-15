> ✈ Flight System AI — 吉利航空智能后台管理系统
> 基于 Spring Cloud Alibaba 微服务架构打造的航空业务后台管理平台，集成 Spring AI 实现自然语言驱动业务操作，支持RAG知识库检索、AI工具调用、RBAC权限管控。

## 📋 项目概览
本系统是面向航空业务的智能后台管理平台，采用前后端分离微服务架构，集成大模型AI助手，管理人员可通过自然语言完成航班、订单、机组、权限等业务查询与操作。

| 项目特性 | 技术实现说明 |
|--------|------------|
| 微服务框架 | Spring Cloud Alibaba 2025.0.0.0 + Spring Boot 3.5.4 |
| AI智能助手 | Spring AI 1.0 + 阿里通义千问 qwen‑plus |
| RAG知识库 | Redis VectorStore + text‑embedding‑v3向量检索 |
| 权限体系 | Spring Security + JWT + RBAC菜单权限模型 |
| 前端技术 | Vue3 + Element Plus + Vite |
| 数据存储 | MyBatis‑Plus + MySQL8 + Redis7 |
| 注册配置 | Nacos 2.x |
| API网关 | Spring Cloud Gateway，内置JWT鉴权过滤器 |

## 🏗 系统架构
```text
                    ┌──────────────┐
                    │   Nacos      │
                    │  注册/配置    │
                    └──────┬───────┘
                           │
┌──────────┐ ┌──────────────┴──────────────┐ ┌──────────────┐
│ Vue 3    │───▶│ Gateway Server           │───▶│ Auth Server  │
│ Frontend │    │ (JWT鉴权 + 路由转发)     │    │ (认证授权)   │
└──────────┘    └──────────────┬──────────────┘    └──────────────┘
                               │
        ┌──────────┬───────────┼───────────┬──────────┐
        ▼          ▼           ▼           ▼          ▼
┌────────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐
│Flight‑Booking│Order  ││Addr   ││Aircrew││Manager │
│航班预订服务 ││订单服务││地址服务││机组服务││系统管理│
└────────────┘ └────────┘ └────────┘ └────────┘ └────────┘
        │          │          │          │          │
        └──────────┴──────────┴──────────┴──────────┘
                               │
                    ┌──────────┴──────────┐
                    │ Spring AI Server    │
                    │ AI公共配置模块       │
                    └─────────────────────┘
```

## 📦 模块说明
### 基础设施层 `cloud‑iaas`
| 模块 | 端口 | 功能说明 |
|---|---|---|
| gateway‑server | 8080 | API网关，JWT Token校验、请求路由转发 |
| auth‑server | 8081 | 认证授权服务，JWT签发，支持管理员/会员双登录策略 |

### 业务服务层 `bussiness‑server`
| 模块 | 端口 | 功能说明 | AI工具能力 |
|---|---|---|---|
| flight‑booking | 9001 | 航班预订、航班信息、客机、航班价格管理 | 航班预订增删改查，航班/客机/价格查询 |
| order‑server | 9003 | 乘客、订单、优惠券管理 | 乘客查询、订单查询/取消、优惠券查询 |
| addr‑server | 9004 | 开通城市信息管理 | 城市信息查询 |
| aircrew‑server | 9002 | 机组人员、人员分组、日程管理 | - |
| manager‑server | 9005 | 系统用户、角色、菜单权限、操作日志管理 | 系统管理辅助 |

### 公共模块 `power‑common`
| 模块 | 说明 |
|---|---|
| common‑base | 统一返回封装R、业务枚举、分页工具、全局异常处理器 |
| common‑core | JWT工具、Redis配置、Security基础安全配置 |

### AI模块 `spring‑ai‑server`
| 组件 | 说明 |
|---|---|
| SpringAiAutoConfiguration | 对话记忆Redis(TTL2天)、Redis向量库、RestClient超时统一配置 |
| TtlRedisChatMemoryRepository | Redis存储对话上下文，会话自动过期清理 |

## 🤖 AI智能助手
> 各业务服务独立内置AI助手，基于Spring AI ChatClient实现，支持对话记忆、RAG知识库增强、工具调用，AI执行操作同样受权限约束。

### AI调用链路
```text
用户输入 → 前端 → AssistantController → ChatClient
→ PromptChatMemoryAdvisor(会话记忆，Redis，保留最近20轮对话)
→ RetrievalAugmentationAdvisor(RAG向量检索 topK=4)
→ ToolCalling调用业务工具 → AI生成结果返回前端
```

### AI工具能力
#### 航班预订服务 FlightBookingAiTools
- `queryBookings`：条件查询航班预订记录
- `addBookingRecord`：新增航班预订
- `updateBookingRecord`：修改舱位等级（改签）
- `cancelBooking`：取消预订（退票）
- `recoveryBookingRecord`：恢复已取消预订
- `queryFlightList`：查询航班信息
- `queryPlaneList`：查询客机信息
- `queryFlightPriceList`：查询航班价格

#### 订单服务 OrderAiTools
- `queryPassengerList`：条件查询乘客信息
- `queryPassengerOrderVo`：乘客订单详情（航班+优惠券）
- `queryOrderList`：条件查询订单列表
- `cancelOrder`：取消订单
- `queryCouponList`：查询优惠券列表

### RAG知识增强
- 向量存储：Redis VectorStore，索引`power‑index`
- Embedding模型：阿里DashScope `text‑embedding‑v3`
- 检索参数：TopK=4，相似度阈值0.0
- 业务文档：各服务`resources/rag`目录存放业务知识库文档

## 🔐 权限体系
### 完整认证流程
```text
登录请求 → AuthServer账号密码校验 → 签发JWT →前端保存accessToken
→ 请求携带Authorization请求头 → Gateway网关过滤器校验Token
→ 请求转发业务服务 → @PreAuthorize校验接口权限 → 业务执行/拒绝访问
```

### RBAC权限模型
`系统用户(SysUser)` → 分配角色`SysRole` → 角色绑定菜单权限`SysMenu`
- 权限层级：目录 → 菜单 → 按钮操作权限标识
- 前端：`v‑if="hasPermission('xxx:yyy')"`控制按钮菜单显示
- 后端：`@PreAuthorize("hasAuthority('xxx:yyy')")`接口鉴权

### AI助手权限
AI助手使用内置`ai‑assistant`身份，通过`SecurityContextHolder`注入预定义权限集合，AI调用业务工具会走完整权限校验逻辑。

## 💻 前端页面功能
| 页面 | 路由 | 功能 |
|---|---|---|
| 首页 | home | 系统数据概览 |
| 客户航班信息 | flight | 航班预订记录查询管理 |
| 具体航班信息 | flight‑detail | 航班详情查看 |
| 客机信息 | plane‑list | 客机信息维护 |
| 乘客信息管理 | passenger‑list | 乘客增删改查 |
| 订单信息管理 | order‑list | 订单列表与详情 |
| 优惠券管理 | coupon‑list | 优惠券维护 |
| 开通城市信息 | addr | 城市信息管理 |
| 机组人员管理 | aircrew | 机组、分组、日程管理 |
| 用户管理 | user‑manage | 系统管理员账号维护 |
| 权限管理 | permission‑manage | 角色权限树配置 |
| AI助手 | 右侧悬浮面板 | 自然语言对话，自动路由对应业务服务 |

## 🛠 技术栈
### 后端
| 技术 | 版本 | 用途 |
|---|---|---|
| Spring Boot | 3.5.4 | 应用框架 |
| Spring Cloud | 2025.0.0 | 微服务治理 |
| Spring Cloud Alibaba | 2025.0.0.0 | Nacos注册配置 |
| Spring AI | 1.0.0 | AI大模型集成 |
| DashScope | - | 通义千问大模型 |
| Spring Security | 6.x | 安全认证授权 |
| MyBatis‑Plus | 3.5.10.1 | ORM持久层 |
| MySQL | 8.x | 业务数据库 |
| Redis | 7.x | 缓存、向量库、会话记忆 |
| Nacos | 2.x | 注册&配置中心 |
| Spring Cloud Gateway | - | API网关 |
| JDK | 17 | 运行环境 |

### 前端
| 技术 | 版本 | 用途 |
|---|---|---|
| Vue | 3.4 | 前端框架 |
| Element Plus | 2.8 | UI组件库 |
| Vue Router | 4.6 | 路由管理 |
| Axios | 1.18 | HTTP请求 |
| Vite | 8.1 | 工程构建 |

## 🚀 快速开始
### 环境依赖
- JDK 17+
- Maven 3.8+
- Node.js 18+
- MySQL8.0+
- Redis7.0+
- Nacos 2.x

#### 1.初始化数据库
```bash
mysql -u root -p < sql/fly.sql
```

#### 2.配置Nacos
进入Nacos控制台，导入`nacos‑config`目录配置文件
> 命名空间ID：`9360894e‑f2bc‑432e‑9e0b‑33e3b30460e4`

#### 3.配置大模型API Key
设置环境变量配置通义千问密钥
```bash
export QWEN_API_KEY=your_dashscope_api_key
```

#### 4.后端启动顺序
```bash
# 1.启动认证服务
cd cloud‑iaas/auth-server && mvn spring‑boot:run

# 2.启动网关服务
cd cloud‑iaas/gateway-server && mvn spring‑boot:run

# 3.并行启动各个业务服务
cd bussiness‑server/flight‑booking && mvn spring‑boot:run
cd bussiness‑server/order-server && mvn spring‑boot:run
cd bussiness‑server/addr-server && mvn spring‑boot:run
cd bussiness‑server/aircrew-server && mvn spring‑boot:run
cd bussiness‑server/manager-server && mvn spring‑boot:run
```

#### 5.启动Vue前端
```bash
cd bussiness‑server/flight‑booking/frontend
npm install
npm run dev
```
访问地址：`[http://localhost:5173](http://localhost:5173)`

## 📁 项目目录结构
```text
Flight‑demo/
├── cloud‑iaas/                # 基础设施服务
│   ├── auth‑server/           # 认证授权服务
│   └── gateway‑server/         # API网关
├── bussiness‑server/          # 业务微服务
│   ├── flight‑booking/        # 航班预订服务，包含前端
│   │   └── frontend/          # Vue3前端工程
│   ├── order‑server/          # 订单服务
│   ├── addr‑server/           # 城市地址服务
│   ├── aircrew‑server/        # 机组人员服务
│   ├── manager‑server/        # 系统管理服务
│   └── spring‑ai‑server/      # AI公共配置模块
├── power‑common/              # 公共依赖模块
│   ├── common‑base/           # 基础封装、异常、枚举
│   └── common‑core/           # JWT、Redis、Security配置
├── nacos‑config/              # Nacos配置导出文件
├── sql/                       # MySQL数据库脚本
└── pom.xml                    # 项目根maven父pom
```

## 📄 License
MIT License
```
