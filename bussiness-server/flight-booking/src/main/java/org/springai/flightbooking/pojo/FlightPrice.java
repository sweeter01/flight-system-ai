package org.springai.flightbooking.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springai.flightbooking.data.BookingClass;

@TableName("tb_flight_price")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightPrice {
    @TableId(type = IdType.AUTO)
    private Long id;

    private  String flightId;

    private BookingClass seatType;

    private  Double price;
    private  Integer status;

    private  Integer seatNum;

    private  Integer saleNum;

    @TableLogic
    private Integer isDeleted;

}