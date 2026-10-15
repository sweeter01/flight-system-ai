import request from '../utils/request.js';

export const getBookingsRequest = (params) => {
    return request.get('/flight-booking/api/bookings', { params });
}

export const addBookingRequest = (params) => {
    return request.post('/flight-booking/', null, { params });
}

export const changeBookingRequest = (params) => {
    return request.put('/flight-booking/api/changeBooking', null, { params });
}

export const cancelBookingRequest = (bookingNumbers) => {
    return request.delete(`/flight-booking/api/cancelBooking/${bookingNumbers.join(',')}`);
}

export const recoveryBookingRequest = (params) => {
    return request.put('/flight-booking/api/recoveryBooking', null, { params });
}

export const getFlightsRequest = (params) => {
    return request.get('/flight-booking/flight/getFlight', { params });
}

export const addFlightRequest = (params) => {
    return request.post('/flight-booking/flight/addFlight', null, { params });
}

export const updateFlightRequest = (params) => {
    return request.put('/flight-booking/flight/updateFlight', null, { params });
}

export const deleteFlightRequest = (id) => {
    return request.delete('/flight-booking/flight/deleteFlight', { params: { id } });
}

export const getPlanesRequest = (params) => {
    return request.get('/flight-booking/plane/getPlaneInfo', { params });
}

export const addPlaneRequest = (params) => {
    return request.post('/flight-booking/plane/addPlaneInfo', null, { params });
}

export const updatePlaneRequest = (params) => {
    return request.post('/flight-booking/plane/updatePlaneInfo', null, { params });
}

export const deletePlaneRequest = (id) => {
    return request.post('/flight-booking/plane/deletePlaneInfo', null, { params: { id } });
}