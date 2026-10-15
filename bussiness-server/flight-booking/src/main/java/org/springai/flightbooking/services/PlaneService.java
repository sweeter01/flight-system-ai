package org.springai.flightbooking.services;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springai.flightbooking.pojo.Plane;

import java.util.List;

public interface PlaneService extends IService<Plane> {


    List<Plane>   selectPlaneByCondition(Long id, String planeType);
    void updateByPlaneId(Plane plane);
    void  deletePlaneById(Long id);
    void addPlane(Plane plane);
}
