package com.power.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.domain.SysLog;
import com.power.mapper.SysLogMapper;
import com.power.service.SysLogService;
import org.springframework.stereotype.Service;

@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper,SysLog> implements SysLogService {
}
