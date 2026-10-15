package com.power.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.power.pojo.Order;
import com.power.vo.OrderVo;

import java.util.List;

public interface OrderService extends IService<Order> {

    Page<Order> getOrderPages(String orderCode,
                                  String passengerName,
                                  String status,String paymentMethod,
                                  Integer current, Integer size);
    void removeOrderByIds(List<Long> ids);

    OrderVo getOrderVoByOrderID(Long id);
}
