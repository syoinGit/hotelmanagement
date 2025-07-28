package com.portfolio.hotel.management.service.converter;

import static org.assertj.core.api.Assertions.assertThat;

import com.portfolio.hotel.management.data.booking.Booking;
import com.portfolio.hotel.management.data.guest.Guest;
import com.portfolio.hotel.management.data.guest.GuestDetail;
import com.portfolio.hotel.management.data.guest.GuestSearch;
import com.portfolio.hotel.management.data.reservation.Reservation;
import com.portfolio.hotel.management.data.reservation.ReservationStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HotelConverterTest {

  private HotelConverter sut;

  @BeforeEach
  void before() {
    sut = new HotelConverter();
  }

  @Test
  void コンバーターを使用して宿泊者と宿泊プランと宿泊予約を統合した宿泊者情報が生成できる() {
    Guest guest = getGuest();
    guest.setId("11111111-1111-1111-1111-111111111111");

    Booking booking = getBooking();
    booking.setId("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    Reservation reservation = getReservation();

    List<Guest> guestList = List.of(guest);
    List<Booking> bookingList = List.of(booking);
    List<Reservation> reservationList = List.of(reservation);

    List<GuestDetail> actual = sut.convertGuestDetail(guestList, bookingList,
        reservationList);

    GuestDetail result = new GuestDetail();
    result.setGuest(actual.getFirst().getGuest());
    result.setBookings(actual.getFirst().getBookings());
    result.setReservations(actual.getFirst().getReservations());

    assertThat(result.getGuest().getName()).isEqualTo("佐藤花子");
    assertThat(result.getReservations().getFirst().getMemo()).isEqualTo("観光利用");

  }

  @Test
  void コンバーターを使用して_宿泊者検索を宿泊者に変換できる() {
    GuestSearch guestSearch = new GuestSearch();
    guestSearch.setName("佐藤花子");
    guestSearch.setKanaName("サトウハナコ");
    guestSearch.setPhone("08098765432");

    Guest actual = sut.toGuest(guestSearch);

    assertThat(actual.getName()).isEqualTo("佐藤花子");
    assertThat(actual.getKanaName()).isEqualTo("サトウハナコ");

  }

  private Guest getGuest() {
    Guest guest = new Guest();

    guest.setName("佐藤花子");
    guest.setKanaName("サトウハナコ");
    guest.setGender("FEMALE");
    guest.setAge(28);
    guest.setRegion("東京");
    guest.setEmail("hanako@example.com");
    guest.setPhone("08098765432");
    return guest;
  }

  private Booking getBooking() {
    Booking booking = new Booking();
    booking.setName("和洋朝食付きプラン");
    booking.setDescription("和食・洋食が選べる朝食付きのプランです。");
    booking.setPrice(BigDecimal.valueOf(1000));
    return booking;
  }

  private Reservation getReservation() {
    Reservation reservation = new Reservation();
    reservation.setGuestId("11111111-1111-1111-1111-111111111111");
    reservation.setBookingId("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    reservation.setCheckInDate(LocalDate.of(2025, 7, 22));
    reservation.setStayDays(2);
    reservation.setTotalPrice(BigDecimal.valueOf(1000));
    reservation.setStatus(
        ReservationStatus.NOT_CHECKED_IN); // enum であれば ReservationStatus.NOT_CHECKED_IN など
    reservation.setMemo("観光利用");
    reservation.setCreatedAt(LocalDateTime.now());
    return reservation;
  }
}