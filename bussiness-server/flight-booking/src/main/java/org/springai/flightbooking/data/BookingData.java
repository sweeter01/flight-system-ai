package org.springai.flightbooking.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingData {

	private List<Customer> customers = new ArrayList<>();

	private List<Booking> bookings = new ArrayList<>();



}
