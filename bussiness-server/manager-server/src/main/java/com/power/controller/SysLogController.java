package com.power.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.power.domain.SysLog;
import com.power.model.Result;
import com.power.service.SysLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "系统操作日志接口管理")
@RequestMapping("sys/log")
@RestController
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;

    @ApiOperation("多条件分页查询系统操作日志")
    @GetMapping("page")
    @PreAuthorize("hasAuthority('sys:log:page')")
    public Result<Page<SysLog>> loadSysLogPage(
            Long current,
            Long size,
            Long userId,
            String operation) {

        Page<SysLog> sysLogPage = new Page<>(current==null?1:current, size==null?10:size);;

        Page<SysLog> paged = sysLogService.page(sysLogPage, new LambdaQueryWrapper<SysLog>()
                .eq(ObjectUtil.isNotNull(userId),SysLog::getUserId, userId)
                .like(StringUtils.hasText(operation), SysLog::getOperation, operation)
                .orderByDesc(SysLog::getCreateDate)
        );
        return  Result.success(paged);
    }
}