package org.springai.flightbooking.client;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.power.model.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springai.flightbooking.pojo.Plane;
import org.springai.flightbooking.services.PlaneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;


@Api("航空公司客机信息")
@RestController
@RequestMapping("/plane")
@Slf4j
public class PlaneController {

    @Autowired
    private PlaneService planeService;

    @ApiOperation(value = "查询客机信息")
    @GetMapping("/getPlaneInfo")
    @PreAuthorize("hasAuthority('plane:getPlaneInfo')")
    public Result<Page<Plane>> getPlaneInfo(
            Long id,
            String planeType,
            Long current,Long size
            ){
        log.info("查询客机信息: id={}, planeType={}", id, planeType);
        Page<Plane> planePage = new Page<>(current == null ? 1 : current, size==null?10:size);
        planePage= planeService.page(planePage, new LambdaQueryWrapper<Plane>()
                .eq(id != null && id > 0, Plane::getId, id)
                .like(planeType != null && !planeType.isEmpty(),Plane::getPlaneType,planeType));
        return Result.success(planePage);
    }

    @ApiOperation(value = "新增客机信息")
    @PostMapping("/addPlaneInfo")
    @PreAuthorize("hasAuthority('plane:addPlaneInfo')")
    public Result addPlaneInfo(Plane plane)
    {
        log.info("新增客机信息: {}", plane);
        planeService.addPlane(plane);
        return Result.success();
    }
    @ApiOperation(value = "删除客机信息")
    @DeleteMapping("/deletePlaneInfo")
    @PreAuthorize("hasAuthority('plane:deletePlaneInfo')")
    public Result deletePlaneInfo(Long id)
    {
        log.info("删除客机信息: {}", id);
        planeService.deletePlaneById(id);
        return Result.success();
    }
    @ApiOperation(value = "更新客机信息")
    @PutMapping("/updatePlaneInfo")
    @PreAuthorize("hasAuthority('plane:updatePlaneInfo')")
    public Result updatePlaneInfo(Plane plane)
    {
        log.info("更新客机信息: {}", plane);
        planeService.updateByPlaneId( plane);
        return Result.success();
    }
}