package org.springai.flightbooking.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springai.flightbooking.data.BookingStatus;

import java.time.LocalDateTime;

@TableName("tb_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingRecord {
    @TableId(type = IdType.AUTO)
    @JsonPropertyDescription("预订号")
    private Long bookingNumber;


    @JsonPropertyDescription("航班批次编号")
    private String flightId;


    @JsonPropertyDescription("乘客ID")
    private Long passengerId;

    @JsonPropertyDescription("乘客姓名")
    private String name;

    @JsonPropertyDescription("日期，格式yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    private BookingStatus bookingStatus;

    @TableField("`from`")
    @JsonPropertyDescription("出发地")
    private String from;

    @TableField("`to`")
    @JsonPropertyDescription("目的地")
    private String to;

    @JsonPropertyDescription("证件号后6位")
    private String certificateSubstring;

    @JsonPropertyDescription("舱位等级，可选值：ECONOMY/PREMIUM_ECONOMY/BUSINESS/FIRST")
    private String bookingClass;

    @TableLogic
    private Integer isDeleted;
}