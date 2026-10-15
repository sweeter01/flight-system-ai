package com.power.vo;


import com.power.domain.SysMenu;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("菜单和操作权限")
public class MenuAndAuthVo {

    @ApiModelProperty("菜单集合")
    private Set<SysMenu> menuList;

    @ApiModelProperty("权限集合")
    private Set<String> authorities;

}
