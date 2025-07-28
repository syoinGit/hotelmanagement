package com.portfolio.hotel.management.service.converter;

import com.portfolio.hotel.management.data.booking.Booking;
import com.portfolio.hotel.management.data.guest.Guest;
import com.portfolio.hotel.management.data.guest.GuestDetail;
import com.portfolio.hotel.management.data.guest.GuestSearch;
import com.portfolio.hotel.management.data.reservation.Reservation;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class HotelConverter {

  // 宿泊者、宿泊プラン、宿泊予約を宿泊者情報に統合
  public List<GuestDetail> convertGuestDetail(List<Guest> guests,
      List<Booking> bookings, List<Reservation> reservations) {

    List<GuestDetail> guestDetails = new ArrayList<>();

    for (Guest guest : guests) {
      GuestDetail guestDetail = new GuestDetail();
      guestDetail.setGuest(guest);

      List<Reservation> matchedReservations = reservations.stream()
          .filter(s -> s.getGuestId().equals(guestDetail.getGuest().getId()))
          .toList();
      guestDetail.setReservations(matchedReservations);

      List<String> bookingIds = guestDetail.getReservations().stream()
          .map(Reservation::getBookingId)
          .distinct()
          .toList();

      List<Booking> matchBookings = bookings.stream()
          .filter(s -> bookingIds.contains(s.getId()))
          .toList();

      guestDetail.setBookings(matchBookings);

      guestDetails.add(guestDetail);
    }
    return guestDetails;
  }

  // 宿泊者検索から宿泊者に変換
  public Guest toGuest(GuestSearch guestSearch) {
    Guest guest = new Guest();
    guest.setName(guestSearch.getName());
    guest.setKanaName(guestSearch.getKanaName());
    guest.setPhone(guestSearch.getPhone());
    return guest;
  }
}