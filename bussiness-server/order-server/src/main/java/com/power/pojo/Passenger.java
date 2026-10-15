package com.power.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.PrivateKey;
import java.time.LocalDateTime;

@TableName("tb_passenger")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Passenger {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer age;
    private String sex;
    private String phone;
    private String email;
    private String certificate;
    private String username;
    @JsonIgnore
    private String password;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;

}