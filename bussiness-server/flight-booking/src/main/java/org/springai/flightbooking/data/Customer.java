package org.springai.flightbooking.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

	private String name;

	private List<Booking> bookings = new ArrayList<>();

}