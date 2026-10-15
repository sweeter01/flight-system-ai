package org.springai.flightbooking.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("tb_plane")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Plane {

    @TableId(type = IdType.INPUT)
    private Long id;

    private String planeType;

    private Integer totalSeats;

    private Integer status;

    @TableLogic
    private Integer isDeleted;
}