package com.power.model;

import com.power.constant.BusinessEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="返回对象")
public class Result<T> {
    @ApiModelProperty(value="状态码")
    private Integer code=200;
    @ApiModelProperty(value="消息")
    private String message="ok";
    @ApiModelProperty(value="数据")
    private T data;


    public static <T> Result<T> success(T data){
        Result<T> result = new Result<>();
        result.setData(data);
        return result;
    }
    public static <T> Result<T> success(){
        return success(null);
    }
    public static <T> Result<T> fail(BusinessEnum businessEnum, String message){
        Result<T> result = new Result<>();
        result.setCode(businessEnum.getCode());
        result.setMessage(message);
        return result;
    }
    public  static <T> Result<T> fail(BusinessEnum businessEnum){

        Result<T> result = new Result<>();
        result.setCode(businessEnum.getCode());
        result.setMessage(businessEnum.getDesc());

        return  result;
    }

    public static Result<String> handle(boolean b) {
        return b ? Result.success() : Result.fail(BusinessEnum.OPERATION_FAIL);
    }
}
