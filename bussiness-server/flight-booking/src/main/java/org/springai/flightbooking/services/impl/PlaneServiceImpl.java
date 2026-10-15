package org.springai.flightbooking.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.exception.handler.BusinessException;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springai.flightbooking.mapper.PlaneMapper;
import org.springai.flightbooking.pojo.Plane;
import org.springai.flightbooking.services.PlaneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaneServiceImpl extends ServiceImpl<PlaneMapper, Plane> implements PlaneService {

    @Autowired
    private  PlaneMapper planeMapper;
    @Override
    public List<Plane> selectPlaneByCondition(Long id, String planeType) {

        return planeMapper.selectList(new LambdaQueryWrapper<Plane>()
                .eq(id > 0, Plane::getId, id)
                .eq(planeType != null && !planeType.isEmpty(), Plane::getPlaneType, planeType));
    }

    @Override
    public void updateByPlaneId(Plane plane) {
        int updated = planeMapper.updateById(plane);
        if (updated == 0)
            throw new BusinessException("更新客机信息失败,请联系管理员！");
    }

    @Override
    public void deletePlaneById(Long id) {
        int deleted = planeMapper.deleteById(id);

        if (deleted == 0) {
            throw new BusinessException("删除客机信息失败,请联系管理员！");


        }

    }

    @Override
    public void addPlane(Plane plane) {
        int inserted = planeMapper.insert(plane);
        if (inserted==0)
            throw  new BusinessException("添加客机信息失败,请联系管理员！");
    }

}
