package com.portfolio.hotel.management.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.portfolio.hotel.management.data.booking.Booking;
import com.portfolio.hotel.management.data.guest.Guest;
import com.portfolio.hotel.management.data.guest.GuestMatch;
import com.portfolio.hotel.management.data.guest.GuestSearchCondition;
import com.portfolio.hotel.management.data.reservation.Reservation;
import com.portfolio.hotel.management.data.reservation.ReservationStatus;
import com.portfolio.hotel.management.data.user.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;


@MybatisTest
class HotelRepositoryTest {

  @Autowired
  HotelRepository sut;

  @Test
  void 宿泊者の全件検索ができること() {
    List<Guest> actual = sut.findAllGuest();
    assertThat(actual.size()).isEqualTo(2);
  }

  @Test
  void 宿泊プランの全件検索ができること() {
    List<Booking> actual = sut.findAllBooking();
    assertThat(actual.size()).isEqualTo(2);

  }

  @Test
  void 宿泊者情報の全件検索ができること() {
    List<Reservation> actual = sut.findAllReservation();
    assertThat(actual.size()).isEqualTo(2);
  }

  @Test
  void 本日チェックイン予定の宿泊者情報を検索() {
    LocalDate today = LocalDate.of(2025, 7, 24);
    List<Guest> actual = sut.findGuestsTodayCheckIn(, today);
    assertThat(actual.size()).isEqualTo(1);
  }

  @Test
  void 本日チェックイン予定の宿泊予約を検索() {
    LocalDate today = LocalDate.of(2025, 7, 24);
    List<Reservation> actual = sut.findReservationTodayCheckIn(, today);
    assertThat(actual.size()).isEqualTo(1);
  }

  @Test
  void 宿泊者の単一検索_IDから宿泊者を検索できるか確認() {
    GuestSearchCondition guestSearchCondition = new GuestSearchCondition();
    guestSearchCondition.setName("佐藤花子");
    List<Guest> actual = sut.searchGuest(, guestSearchCondition);

    Guest result = actual.getFirst();
    assertThat(actual.size()).isEqualTo(1);
    assertThat(result.getName()).isEqualTo("佐藤花子");
    assertThat(result.getKanaName()).isEqualTo("サトウハナコ");
    assertThat(result.getGender()).isEqualTo("FEMALE");

  }

  @Test
  void 宿泊者の単一検索_IDから宿泊者を検索できているか確認() {
    String id = "11111111-1111-1111-1111-111111111111";
    Guest actual = sut.findByGuestId("11111111-1111-1111-1111-111111111111");

    assertThat(actual.getName()).isEqualTo("佐藤花子");
  }

  @Test
  void 宿泊予約の単一検索_IDから宿泊予約を検索できているか確認() {
    String id = "rsv00001-aaaa-bbbb-cccc-000000000001";

    Reservation actual = sut.searchReservation(id);
    assertThat(actual.getGuestId()).isEqualTo("11111111-1111-1111-1111-111111111111");
    assertThat(actual.getBookingId()).isEqualTo("aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
  }

  @Test
  void 宿泊者の完全一致検索_名前_かな名_電話番号の組み合わせから宿泊者を検索できるか確認() {
    GuestMatch guestMatch = new GuestMatch();
    guestMatch.setName("佐藤花子");
    guestMatch.setKanaName("サトウハナコ");
    guestMatch.setPhone("08098765432");
    Guest actual = sut.matchGuest(, guestMatch);

    assertThat(actual.getId()).isEqualTo("11111111-1111-1111-1111-111111111111");
    assertThat(actual.getGender()).isEqualTo("FEMALE");
  }

  @Test
  void 宿泊プランIDから金額を検索_検索できているか確認() {
    String bookingId = "aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa";
    BigDecimal actual = sut.findTotalPriceById(bookingId);
    assertThat(actual).isEqualTo("10000.00");
  }

  @Test
  void 宿泊予約IDから宿泊予約情報を検索_検索できているか確認() {
    String reservationId = "rsv00001-aaaa-bbbb-cccc-000000000001";
    ReservationStatus actual = sut.findStatusById(reservationId);
    assertThat(actual).isEqualTo(ReservationStatus.CHECKED_IN);
  }

  @Test
  void 宿泊者の登録処理_宿泊者が登録されているか確認() {
    Guest guest = getGuest();
    guest.setId("11111111-1111-1111-1111-111111111115");
    sut.insertGuest(guest);

    List<Guest> actual = sut.findAllGuest();
    assertThat(actual.size()).isEqualTo(3);
  }

  @Test
  void 宿泊プランの登録_宿泊プランが登録されているか確認() {
    Booking booking = getBooking();
    sut.insertBooking(booking);

    List<Booking> actual = sut.findAllBooking();
    assertThat(actual.size()).isEqualTo(3);
  }

  @Test
  void 宿泊情報の登録_宿泊情報が登録されているか確認() {
    Reservation reservation = getReservation();
    sut.insertReservation(reservation);

    List<Reservation> actual = sut.findAllReservation();
    assertThat(actual.size()).isEqualTo(3);
  }

  @Test
  void 宿泊者情報の変更_宿泊者情報が変更されている() {
    Guest guest = getGuest();
    GuestSearchCondition guestSearchCondition = new GuestSearchCondition();

    guest.setId("11111111-1111-1111-1111-111111111111");
    guest.setName("佐藤華子");
    sut.updateGuest(guest);

    List<Guest> actual = sut.searchGuest(guestSearchCondition);
  }

  @Test
  void 宿泊予約の変更_宿泊予約が変更されていること() {
    Reservation reservation = getReservation();
    reservation.setId("rsv00001-aaaa-bbbb-cccc-000000000001");
    sut.updateReservation(reservation);

    Reservation actual = sut.searchReservation("rsv00001-aaaa-bbbb-cccc-000000000001");
    assertThat(actual.getStatus()).isEqualTo(reservation.getStatus());
  }

  @Test
  void 宿泊者の論理削除_削除フラグがtureになっていること() {
    String id = "11111111-1111-1111-1111-111111111111";
    sut.logicalDeleteGuest(id);

    GuestSearchCondition guestSearchCondition = new GuestSearchCondition();
    guestSearchCondition.setName("佐藤花子");
    Guest actual = sut.findByGuestId(id);

    assertThat(actual.getDeleted()).isTrue();
  }

  @Test
  void チェックイン処理_ステータスがチェックインに変わっているか確認() {
    String reservationId = "rsv00001-aaaa-bbbb-cccc-000000000001";
    sut.checkIn(reservationId);
    Reservation actual = sut.searchReservation(reservationId);

    assertThat(actual.getStatus()).isEqualTo(ReservationStatus.CHECKED_IN);
  }

  @Test
  void チェックイン処理_ステータスがチェックアウトに変わっているか確認() {
    String reservationId = "rsv00002-bbbb-cccc-dddd-000000000002";
    sut.checkOut(reservationId);
    Reservation actual = sut.searchReservation(reservationId);

    assertThat(actual.getStatus()).isEqualTo(ReservationStatus.CHECKED_OUT);
  }

  @Test
  void ユーザーの登録処理_ユーザーが登録されていること() {
    User actual = new User();
    actual.setId("123");
    actual.setPassword("123");

    sut.insertUser(actual);
    User update = sut.findUserById("123");

    assertThat(actual.getId()).isEqualTo(update.getId());

  }


  // 生成用
  private Guest getGuest() {
    Guest guest = new Guest();
    guest.setName("佐藤華子");
    guest.setKanaName("サトウハナコ");
    guest.setGender("FEMALE");
    guest.setAge(28);
    guest.setEmail("hanako@example.com");
    guest.setPhone("08098765432");
    return guest;
  }

  private Booking getBooking() {
    Booking booking = new Booking();
    booking.setId("aaaaaaa3-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    booking.setName("朝食付きプラン");
    booking.setPrice(BigDecimal.valueOf(1000));
    booking.setDescription("");

    return booking;
  }

  private Reservation getReservation() {
    Reservation reservation = new Reservation();
    reservation.setId("aaaaaaa5-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    reservation.setGuestId("11111111-1111-1111-1111-111111111111");
    reservation.setBookingId("aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    reservation.setTotalPrice(BigDecimal.valueOf(2000));
    reservation.setCheckInDate(LocalDate.now());
    reservation.setStayDays(1);
    reservation.setCheckOutDate(LocalDate.now().plusDays(reservation.getStayDays()));
    reservation.setStatus(ReservationStatus.NOT_CHECKED_IN);
    reservation.setMemo("");
    return reservation;
  }
}