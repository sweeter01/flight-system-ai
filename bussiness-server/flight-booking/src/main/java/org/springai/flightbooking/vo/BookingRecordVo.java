package org.springai.flightbooking.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springai.flightbooking.pojo.BookingRecord;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRecordVo extends BookingRecord {

    @JsonIgnore
    private String  certificateSubstring;
    private  Double price;
}
