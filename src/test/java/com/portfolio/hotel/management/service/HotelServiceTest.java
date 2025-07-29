package com.portfolio.hotel.management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.portfolio.hotel.management.data.guest.GuestMatch;
import com.portfolio.hotel.management.data.guest.GuestRegistration;
import com.portfolio.hotel.management.data.guest.GuestSearchCondition;
import com.portfolio.hotel.management.service.converter.HotelConverter;
import com.portfolio.hotel.management.data.booking.Booking;
import com.portfolio.hotel.management.data.guest.Guest;
import com.portfolio.hotel.management.data.guest.GuestDetail;
import com.portfolio.hotel.management.data.reservation.Reservation;
import com.portfolio.hotel.management.data.reservation.ReservationStatus;
import com.portfolio.hotel.management.repository.HotelRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

  private static final Logger log = LogManager.getLogger(HotelServiceTest.class);
  @Mock
  private HotelRepository repository;

  @Mock
  private HotelConverter converter;

  @Mock
  Clock clock;

  @Test
  void 宿泊者情報の全件検索機能_リポジトリとコンバーターが呼び出せていること() {
    HotelService sut = new HotelService(repository, converter);

    List<Guest> guest = new ArrayList<>();
    List<Booking> booking = new ArrayList<>();
    List<Reservation> reservation = new ArrayList<>();
    List<GuestDetail> converted = new ArrayList<>();

    when(repository.findAllGuest()).thenReturn(guest);
    when(repository.findAllBooking()).thenReturn(booking);
    when(repository.findAllReservation()).thenReturn(reservation);
    when(converter.convertGuestDetail(guest, booking, reservation))
        .thenReturn(converted);

    List<GuestDetail> actual = sut.getAllGuest();

    verify(repository, Mockito.times(1)).findAllGuest();
    verify(repository, Mockito.times(1)).findAllBooking();
    verify(repository, Mockito.times(1)).findAllReservation();

    verify(converter, Mockito.times(1))
        .convertGuestDetail(guest, booking, reservation);

    assertNotNull(actual);
    assertEquals(actual, converted);

  }

  @Test
  void 宿泊者情報の単一検索機能_ID_名前_電話番号から宿泊者情報が呼び出せていること() {
    HotelService sut = new HotelService(repository, converter);

    GuestSearchCondition guestSearchCondition = new GuestSearchCondition();
    List<Guest> guestList = new ArrayList<>();
    List<Booking> bookingList = new ArrayList<>();
    List<Reservation> reservationList = new ArrayList<>();
    List<GuestDetail> converted = new ArrayList<>();

    guestSearchCondition.setPhone("08098765432");
    guestSearchCondition.setName("佐藤花子");

    when(repository.searchGuest(guestSearchCondition)).thenReturn(guestList);
    when(repository.findAllBooking()).thenReturn(bookingList);
    when(repository.findAllReservation()).thenReturn(reservationList);
    when(converter.convertGuestDetail(guestList, bookingList, reservationList))
        .thenReturn(converted);

    List<GuestDetail> actual = sut.searchGuest(guestSearchCondition);

    verify(repository, Mockito.times(1)).searchGuest(guestSearchCondition);
    verify(repository, Mockito.times(1)).findAllBooking();
    verify(repository, Mockito.times(1)).findAllReservation();
    verify(converter, Mockito.times(1))
        .convertGuestDetail(guestList, bookingList, reservationList);

    assertNotNull(actual);
    assertEquals(converted, actual);
  }

  @Test
  void 宿泊者情報の完全一致致検索_名前_ふりがな_電話番号から宿泊者情報が呼び出せていること() {
    HotelService sut = new HotelService(repository, converter);

    GuestMatch guestMatch = new GuestMatch();
    Guest guest = new Guest();
    guestMatch.setName("佐藤花子");
    guest.setKanaName("サトウハナコ");
    guest.setPhone("08098765432");

    when(repository.matchGuest(guestMatch)).thenReturn(guest);
    GuestDetail actual = sut.matchGuest(guestMatch);

    verify(repository, Mockito.times(1)).matchGuest(guestMatch);
    verify(converter, Mockito.never()).toGuest(Mockito.any());

    assertNotNull(actual);
    assertEquals(guest, actual.getGuest());
  }

  @Test
  void 宿泊者情報の完全一致致検索_完全一致するものがなく条件分岐しているかの確認() {
    HotelService sut = new HotelService(repository, converter);

    GuestMatch guestMatch = new GuestMatch();
    Guest guest = new Guest();

    guestMatch.setName("佐藤花子");
    guestMatch.setKanaName("サトウハナコ");

    when(repository.matchGuest(guestMatch)).thenReturn(null);
    when(converter.toGuest(guestMatch)).thenReturn(guest);

    GuestDetail actual = sut.matchGuest(guestMatch);

    verify(repository, Mockito.times(1)).matchGuest(guestMatch);
    verify(converter, Mockito.times(1)).toGuest(guestMatch);

    assertNotNull(actual);
    assertEquals(guest, actual.getGuest());
  }

  @Test
  void 本日チェックインの宿泊者情報を取得_リポジトリとコンバータが呼び出せていること() {
    HotelService sut = new HotelService(repository, converter);

    List<Guest> guest = new ArrayList<>();
    List<Booking> booking = new ArrayList<>();
    List<Reservation> reservation = new ArrayList<>();
    List<GuestDetail> converted = new ArrayList<>();
    LocalDate today = LocalDate.of(2025, 7, 24);

    when(repository.findGuestsTodayCheckIn(today)).thenReturn(guest);
    when(repository.findAllBooking()).thenReturn(booking);
    when(repository.findReservationTodayCheckIn(today)).thenReturn(reservation);
    when(converter.convertGuestDetail(guest, booking, reservation))
        .thenReturn(converted);

    List<GuestDetail> actual = sut.getChackInToday(today);

    assertNotNull(actual);
    assertEquals(actual, converted);

  }

  @Test
  void ゲスト情報登録_登録が行われているか確認() {
    HotelService sut = new HotelService(repository, converter);
    crateRegistration();

    when(repository.findTotalPriceById("aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa"))
        .thenReturn(new BigDecimal("10000"));
    GuestRegistration actual = crateRegistration();
    sut.registerGuest(actual);

    ArgumentCaptor<Guest> guestCaptor = ArgumentCaptor.forClass(Guest.class);
    verify(repository).insertGuest(guestCaptor.capture());

    ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(
        Reservation.class);
    verify(repository).insertReservation(reservationCaptor.capture());

    Guest updateGuest = guestCaptor.getValue();
    Reservation updateReservation = reservationCaptor.getValue();

    assertEquals("山田太郎", updateGuest.getName());
    assertEquals("aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
        updateReservation.getBookingId());
  }

  @Test
  void ゲスト情報登録_IDが登録済みの場合登録が行われないか確認() {
    HotelService sut = new HotelService(repository, converter);

    GuestRegistration guestRegistration = crateRegistration();
    guestRegistration.getGuest().setId("11111111-1111-1111-1111-111111111111");
    when(repository.findTotalPriceById("aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa"))
        .thenReturn(new BigDecimal("10000"));

    GuestRegistration actual = guestRegistration;
    sut.registerGuest(actual);
    verify(repository, Mockito.times(0)).insertGuest(actual.getGuest());
  }

  @Test
  void 宿泊プランの登録_登録が行われているか確認() {
    HotelService sut = new HotelService(repository, converter);
    Booking booking = createBooking();
    sut.registerBooking(booking);

    ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
    verify(repository).insertBooking(captor.capture());
    Booking update = captor.getValue();

    assertEquals("朝食付きプラン", update.getName());
  }

  @Test
  void 宿泊プランの変更_宿泊情報が変更されているか確認() {
    HotelService sut = new HotelService(repository, converter);
    Guest actual = new Guest();
    actual.setName("山田太郎");

    sut.updateGuest(actual);

    ArgumentCaptor<Guest> captor = ArgumentCaptor.forClass(Guest.class);
    verify(repository).updateGuest(captor.capture());
    Guest update = captor.getValue();

    assertEquals("山田太郎", update.getName());

  }

  @Test
  void 宿泊プランの変更_宿泊予約が変更されているか確認() {
    HotelService sut = new HotelService(repository, converter);
    Reservation actual = new Reservation();
    actual.setTotalPrice(BigDecimal.valueOf(1000));

    sut.updateReservation(actual);

    ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
    verify(repository).updateReservation(captor.capture());
    Reservation update = captor.getValue();

    assertEquals(BigDecimal.valueOf(1000), update.getTotalPrice());

  }

  @Test
  void チェックイン処理の作成_チェックインが行われているかの確認() {
    HotelService sut = new HotelService(repository, converter);
    String reservationId = "3822609c-5651-11f0-b59f-a75edf46bde3";
    when(repository.findStatusById(reservationId))
        .thenReturn(ReservationStatus.NOT_CHECKED_IN);

    sut.checkIn(reservationId);

    verify(repository, Mockito.times(1)).findStatusById(reservationId);
  }

  @Test
  void チェックイン処理の作成_ステータスが未チェックイン以外の場合エラーが発生するかの確認() {
    HotelService sut = new HotelService(repository, converter);
    String reservationId = "3822609c-5651-11f0-b59f-a75edf46bde3";
    when(repository.findStatusById(reservationId))
        .thenReturn(ReservationStatus.CHECKED_IN);

    Assertions.assertThrows(IllegalStateException.class, () -> {
      sut.checkIn(reservationId);
    });

    verify(repository, Mockito.times(1)).findStatusById(reservationId);
  }

  @Test
  void チェックアウト処理の作成_チェックアウトが行われているかの確認() {
    HotelService sut = new HotelService(repository, converter);
    String reservationId = "3822609c-5651-11f0-b59f-a75edf46bde3";
    when(repository.findStatusById(reservationId))
        .thenReturn(ReservationStatus.CHECKED_IN);

    sut.checkOut(reservationId);
    verify(repository, Mockito.times(1)).findStatusById(reservationId);
  }

  @Test
  void チェックアウト処理の作成_ステータスがチェックイン済み以外の場合エラーが発生するかの確認() {
    HotelService sut = new HotelService(repository, converter);
    String reservationId = "3822609c-5651-11f0-b59f-a75edf46bde3";
    when(repository.findStatusById(reservationId))
        .thenReturn(ReservationStatus.CHECKED_OUT);

    Assertions.assertThrows(IllegalStateException.class, () -> {
      sut.checkOut(reservationId);
    });

    verify(repository, Mockito.times(1)).findStatusById(reservationId);
  }

  // 生成用
  private static Guest createGuest() {
    Guest guest = new Guest();

    guest.setName("山田太郎");
    guest.setKanaName("ヤマダタロウ");
    guest.setGender("MALE");
    guest.setAge(30);
    guest.setRegion("青森県");
    guest.setEmail("yamadamori@mail.com");
    guest.setPhone("010-1234-5678");
    return guest;
  }

  private static Booking createBooking() {
    Booking booking = new Booking();

    booking.setId("3822609c-5651-11f0-b59f-a75edf46bde3");
    booking.setName("朝食付きプラン");
    booking.setPrice(BigDecimal.valueOf(1000));
    booking.setDescription("朝食が付いたプランです");
    return booking;
  }

  private GuestRegistration crateRegistration() {
    GuestRegistration guestRegistration = new GuestRegistration();
    Guest guest = createGuest();

    guestRegistration.setGuest(guest);
    guestRegistration.setBookingId("aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    guestRegistration.setCheckInDate(LocalDate.now());
    guestRegistration.setStayDays(1);
    guestRegistration.setMemo("備考なし");

    return guestRegistration;
  }
}