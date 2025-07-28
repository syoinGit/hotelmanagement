package com.portfolio.hotel.management.data.guest;

import com.portfolio.hotel.management.data.booking.Booking;
import com.portfolio.hotel.management.data.reservation.Reservation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GuestDetail {
  private Guest guest;
  private List<Booking> bookings;
  private List<Reservation> reservations;
}