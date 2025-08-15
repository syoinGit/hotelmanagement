package com.portfolio.hotel.management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.portfolio.hotel.management.data.guest.GuestMatch;
import com.portfolio.hotel.management.data.guest.GuestRegistration;
import com.portfolio.hotel.management.data.guest.GuestSearchCondition;
import com.portfolio.hotel.management.data.user.User;
import com.portfolio.hotel.management.service.converter.HotelConverter;
import com.portfolio.hotel.management.data.booking.Booking;
import com.portfolio.hotel.management.data.guest.Guest;
import com.portfolio.hotel.management.data.guest.GuestDetail;
import com.portfolio.hotel.management.data.reservation.Reservation;
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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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
    String userId = getUserId();
    Authentication auth = getAuthentication();

    List<Guest> guest = new ArrayList<>();
    List<Booking> booking = new ArrayList<>();
    List<Reservation> reservation = new ArrayList<>();
    List<GuestDetail> converted = new ArrayList<>();

    when(repository.findAllGuest(userId)).thenReturn(guest);
    when(repository.findAllBooking(userId)).thenReturn(booking);
    when(repository.findAllReservation(userId)).thenReturn(reservation);
    when(converter.convertGuestDetail(guest, booking, reservation))
        .thenReturn(converted);

    List<GuestDetail> actual = sut.getAllGuest(auth);

    verify(repository, times(1)).findAllGuest(userId);
    verify(repository, times(1)).findAllBooking(userId);
    verify(repository, times(1)).findAllReservation(userId);

    verify(converter, times(1))
        .convertGuestDetail(guest, booking, reservation);

    assertNotNull(actual);
    assertEquals(actual, converted);

  }

  @Test
  void 宿泊者情報の単一検索機能_ID_名前_電話番号から宿泊者情報が呼び出せていること() {
    HotelService sut = new HotelService(repository, converter);

    String userId = getUserId();
    Authentication auth = getAuthentication();

    GuestSearchCondition guestSearchCondition = new GuestSearchCondition();
    List<Guest> guestList = new ArrayList<>();
    List<Booking> bookingList = new ArrayList<>();
    List<Reservation> reservationList = new ArrayList<>();
    List<GuestDetail> converted = new ArrayList<>();

    guestSearchCondition.setPhone("08098765432");
    guestSearchCondition.setName("佐藤花子");

    when(repository.searchGuest(guestSearchCondition)).thenReturn(guestList);
    when(repository.findAllBooking(userId)).thenReturn(bookingList);
    when(repository.findAllReservation(userId)).thenReturn(reservationList);
    when(converter.convertGuestDetail(guestList, bookingList, reservationList))
        .thenReturn(converted);

    List<GuestDetail> actual = sut.searchGuest(auth, guestSearchCondition);

    verify(repository, Mockito.times(1)).searchGuest(guestSearchCondition);
    verify(repository, Mockito.times(1)).findAllBooking(userId);
    verify(repository, Mockito.times(1)).findAllReservation(userId);
    verify(converter, Mockito.times(1))

        .convertGuestDetail(guestList, bookingList, reservationList);

    assertNotNull(actual);
    assertEquals(converted, actual);
  }

  @Test
  void 宿泊者情報の完全一致致検索_名前_ふりがな_電話番号から宿泊者情報が呼び出せていること() {
    HotelService sut = new HotelService(repository, converter);
    String userId = getUserId();
    Authentication auth = getAuthentication();

    GuestMatch guestMatch = new GuestMatch();
    Guest guest = new Guest();
    guestMatch.setName("佐藤花子");
    guest.setKanaName("サトウハナコ");
    guest.setPhone("08098765432");

    when(repository.matchGuest(guestMatch)).thenReturn(guest);
    GuestRegistration actual = sut.matchGuest(auth, guestMatch);

    verify(repository, Mockito.times(1)).matchGuest(guestMatch);
    verify(converter, Mockito.never()).toGuest(Mockito.any());

    assertNotNull(actual);
    assertEquals(guest, actual.getGuest());
  }

  @Test
  void 宿泊者情報の完全一致致検索_完全一致するものがなく条件分岐していること() {
    HotelService sut = new HotelService(repository, converter);
    Authentication auth = getAuthentication();

    GuestMatch guestMatch = new GuestMatch();
    Guest guest = new Guest();

    guestMatch.setName("佐藤花子");
    guestMatch.setKanaName("サトウハナコ");

    when(repository.matchGuest(guestMatch)).thenReturn(null);
    when(converter.toGuest(guestMatch)).thenReturn(guest);

    GuestRegistration actual = sut.matchGuest(auth, guestMatch);

    verify(repository, Mockito.times(1)).matchGuest(guestMatch);
    verify(converter, Mockito.times(1)).toGuest(guestMatch);

    assertNotNull(actual);
    assertEquals(guest, actual.getGuest());
  }

  @Test
  void 本日チェックインの宿泊者情報を取得_リポジトリとコンバータが呼び出せていること() {
    HotelService sut = new HotelService(repository, converter);
    String userId = getUserId();
    Authentication auth = getAuthentication();

    List<Guest> guest = new ArrayList<>();
    List<Booking> booking = new ArrayList<>();
    List<Reservation> reservation = new ArrayList<>();
    List<GuestDetail> converted = new ArrayList<>();
    LocalDate today = LocalDate.of(2025, 7, 24);

    when(repository.findGuestsTodayCheckIn(userId, today)).thenReturn(guest);
    when(repository.findAllBooking(userId)).thenReturn(booking);
    when(repository.findReservationTodayCheckIn(userId, today)).thenReturn(reservation);
    when(converter.convertGuestDetail(guest, booking, reservation))
        .thenReturn(converted);

    List<GuestDetail> actual = sut.getChackInToday(auth, today);

    assertNotNull(actual);
    assertEquals(actual, converted);

  }

  @Test
  void ゲスト情報登録_登録が行われていること() {
    HotelService sut = new HotelService(repository, converter);
    Authentication auth = getAuthentication();

    GuestRegistration registration = crateRegistration();
    sut.registerGuest(auth, registration);

    verify(repository, times(1)).insertGuest(any(Guest.class));
    verify(repository, times(1)).insertReservation(any(Reservation.class));
  }

  @Test
  void ゲスト情報登録_IDが登録済みの場合登録が行われないこと() {
    HotelService sut = new HotelService(repository, converter);
    Authentication auth = getAuthentication();

    GuestRegistration actual = crateRegistration();
    actual.getGuest().setId("11111111-1111-1111-1111-111111111111");

    sut.registerGuest(auth, actual);
    verify(repository, never()).insertGuest(any());
    verify(repository, times(1)).insertReservation(any());
  }

  @Test
  void 宿泊プランの登録_リポジトリが呼ばれること() {
    HotelService sut = new HotelService(repository, converter);
    Booking booking = createBooking();

    sut.registerBooking(booking);

    verify(repository).insertBooking(booking);
  }

  @Test
  void 宿泊者情報の更新_リポジトリが呼ばれること() {
    HotelService sut = new HotelService(repository, converter);
    Guest guest = new Guest();
    guest.setName("山田太郎");


  }

  @Test
  void 宿泊プランの変更_リポジトリが呼ばれること() {
    HotelService sut = new HotelService(repository, converter);
    Reservation actual = new Reservation();


  }

  @Test
  void 宿泊者の論理削除_リポジトリが呼び呼び出せていること() {
    HotelService sut = new HotelService(repository, converter);
    String id = "11111111-1111-1111-1111-111111111111";

  }

  @Test
  void チェックイン処理の作成_チェックインが行われていること() {
    HotelService sut = new HotelService(repository, converter);
    String reservationId = "3822609c-5651-11f0-b59f-a75edf46bde3";


  }

  @Test
  void チェックイン処理の作成_ステータスが未チェックイン以外の場合エラーが発生すること() {
    HotelService sut = new HotelService(repository, converter);
    String reservationId = "3822609c-5651-11f0-b59f-a75edf46bde3";
    Assertions.assertThrows(IllegalStateException.class, () -> {
    });

  }

  @Test
  void チェックアウト処理の作成_チェックアウトが行われていること() {
    HotelService sut = new HotelService(repository, converter);
    String reservationId = "3822609c-5651-11f0-b59f-a75edf46bde3";

  }

  @Test
  void チェックアウト処理の作成_ステータスがチェックイン済み以外の場合エラーが発生すること() {
    HotelService sut = new HotelService(repository, converter);
    String reservationId = "3822609c-5651-11f0-b59f-a75edf46bde3";

    Assertions.assertThrows(IllegalStateException.class, () -> {
    });

  }

  @Test
  void ユーザーの登録処理_リポジトリが呼び出せていること() {
    HotelService sut = new HotelService(repository, converter);
    User user = new User();
    user.setId("123");
    user.setPassword("123");

    sut.registerUser(user);
    verify(repository, Mockito.times(1)).insertUser(user);
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

  private static String getUserId() {
    return "TEST";
  }

  private static Authentication getAuthentication() {
    return new UsernamePasswordAuthenticationToken("TEST", "pass",
        List.of(new SimpleGrantedAuthority("ROLE_USER")));
  }
}