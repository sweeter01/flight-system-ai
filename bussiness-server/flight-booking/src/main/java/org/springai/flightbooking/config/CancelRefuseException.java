package org.springai.flightbooking.config;


/*拒绝取消或修改订单异常*/
public class CancelRefuseException extends RuntimeException {
    public CancelRefuseException(String message) {
        super(message);
    }
}
