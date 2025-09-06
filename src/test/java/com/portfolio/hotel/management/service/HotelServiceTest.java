package com.portfolio.hotel.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.portfolio.hotel.management.data.guest.GuestMatch;
import com.portfolio.hotel.management.data.guest.GuestRegistration;
import com.portfolio.hotel.management.data.guest.GuestSearchCondition;
import com.portfolio.hotel.management.data.reservation.ReservationStatus;
import com.portfolio.hotel.management.data.user.User;
import com.portfolio.hotel.management.service.converter.HotelConverter;
import com.portfolio.hotel.management.data.booking.Booking;
import com.portfolio.hotel.management.data.guest.Guest;
import com.portfolio.hotel.management.data.guest.GuestDetail;
import com.portfolio.hotel.management.data.reservation.Reservation;
import com.portfolio.hotel.management.repository.HotelRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

  @Mock
  private HotelRepository repository;

  @Mock
  private HotelConverter converter;

  @Test
  void 宿泊者情報の全件検索_リポジトリとコンバーターが呼び出せている() {
    HotelService sut = new HotelService(repository, converter);
    Authentication auth = getAuthentication();
    String userId = getUserId(auth);

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
  void 宿泊コースの全件検索_リポジトリが呼び出せている() {
    HotelService sut = new HotelService(repository, converter);
    Authentication auth = getAuthentication();
    String userId = getUserId(auth);

    List<Booking> bookings = new ArrayList<>();
    when(repository.findAllBooking(userId)).thenReturn(bookings);

    List<Booking> actual = sut.getAllBooking(auth);
    verify(repository, times(1)).findAllBooking(userId);

    assertNotNull(actual);
  }

  @Test
  void 本日チェックインの宿泊者の検索_リポジトリとコンバーターが呼び出せている() {
    HotelService sut = new HotelService(repository, converter);
    Authentication auth = getAuthentication();
    String userId = getUserId(auth);

    LocalDate today = LocalDate.of(2025, 7, 23);

    List<Guest> guest = new ArrayList<>();
    List<Booking> booking = new ArrayList<>();
    List<Reservation> reservation = new ArrayList<>();
    List<GuestDetail> converted = new ArrayList<>();

    when(repository.findGuestsTodayCheckIn(userId, today)).thenReturn(guest);
    when(repository.findAllBooking(userId)).thenReturn(booking);
    when(repository.findReservationTodayCheckIn(userId, today)).thenReturn(reservation);
    when(converter.convertGuestDetail(guest, booking, reservation))
        .thenReturn(converted);

    List<GuestDetail> actual = sut.getCheckInToday(auth, today);

    verify(repository, times(1)).findGuestsTodayCheckIn(userId, today);
    verify(repository, times(1)).findAllBooking(userId);
    verify(repository, times(1)).findReservationTodayCheckIn(userId, today);
    verify(converter, times(1))
        .convertGuestDetail(guest, booking, reservation);

    assertNotNull(actual);
    assertEquals(actual, converted);
  }

  @Test
  void 現在宿泊中の宿泊者情報の検索_リポジトリとコンバーターが呼び出せている() {
    HotelService sut = new HotelService(repository, converter);
    Authentication auth = getAuthentication();
    String userId = getUserId(auth);

    List<Guest> guest = new ArrayList<>();
    List<Booking> booking = new ArrayList<>();
    List<Reservation> reservation = new ArrayList<>();
    List<GuestDetail> converted = new ArrayList<>();

    when(repository.findGuestStayNow(userId)).thenReturn(guest);
    when(repository.findAllBooking(userId)).thenReturn(booking);
    when(repository.findReservationStayNow(userId)).thenReturn(reservation);
    when(converter.convertGuestDetail(guest, booking, reservation))
        .thenReturn(converted);

    List<GuestDetail> actual = sut.getStayNow(auth);

    verify(repository, times(1)).findGuestStayNow(userId);
    verify(repository, times(1)).findAllBooking(userId);
    verify(repository, times(1)).findReservationStayNow(userId);
    verify(converter, times(1))
        .convertGuestDetail(guest, booking, reservation);

    assertNotNull(actual);
    assertEquals(actual, converted);
  }

  @Test
  void 本日チェックアウトの宿泊者の検索_リポジトリとコンバーターが呼び出せている() {
    HotelService sut = new HotelService(repository, converter);
    Authentication auth = getAuthentication();
    String userId = getUserId(auth);
    LocalDate today = LocalDate.of(2025, 7, 23);

    List<Guest> guest = new ArrayList<>();
    List<Booking> booking = new ArrayList<>();
    List<Reservation> reservation = new ArrayList<>();
    List<GuestDetail> converted = new ArrayList<>();

    when(repository.findGuestsTodayCheckOut(userId, today)).thenReturn(guest);
    when(repository.findAllBooking(userId)).thenReturn(booking);
    when(repository.findReservationTodayCheckOut(userId, today)).thenReturn(reservation);
    when(converter.convertGuestDetail(guest, booking, reservation))
        .thenReturn(converted);

    List<GuestDetail> actual = sut.getCheckOutToday(auth, today);

    verify(repository, times(1)).findGuestsTodayCheckOut(userId, today);
    verify(repository, times(1)).findAllBooking(userId);
    verify(repository, times(1)).findReservationTodayCheckOut(userId, today);
    verify(converter, times(1))
        .convertGuestDetail(guest, booking, reservation);

    assertNotNull(actual);
    assertEquals(actual, converted);
  }

  @Test
  void 宿泊者情報の単一検索機能_リポジトリとコンバーターが呼び出せている() {
    HotelService sut = new HotelService(repository, converter);
    Authentication auth = getAuthentication();
    String userId = getUserId(auth);

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

  @Nested
  @DisplayName("宿泊者情報の完全一致検索")
  class matchGuest {

    @Test
    void リポジトリとコンバーターが呼び出せている() {
      HotelService sut = new HotelService(repository, converter);
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
  }

  @Nested
  @DisplayName("ゲスト情報登録")
  class registerGuest {

    @Test
    void リポジトリとコンバーターが呼び出せている() {
      HotelService sut = new HotelService(repository, converter);
      Authentication auth = getAuthentication();
      String userId = getUserId(auth);
      String id = "aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa";

      when(repository.findTotalPriceById(id, userId))
          .thenReturn(new BigDecimal(10000));

      GuestRegistration registration = crateRegistration();
      sut.registerGuest(auth, registration);

      verify(repository, times(1)).insertGuest(any(Guest.class));
      verify(repository, times(1)).insertReservation(any(Reservation.class));
      verify(repository, times(1)).findTotalPriceById(id, userId);
    }

    @Test
    void ゲスト情報登録_IDが登録済みの場合登録が行われないこと() {
      HotelService sut = new HotelService(repository, converter);
      Authentication auth = getAuthentication();
      GuestRegistration registration = crateRegistration();
      registration.getGuest().setId("11111111-1111-1111-1111-111111111120");

      when(repository.findTotalPriceById("aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa", "TEST"))
          .thenReturn(new BigDecimal(10000));
      sut.registerGuest(auth, registration);

      verify(repository, times(0)).insertGuest(any(Guest.class));
      verify(repository, times(1)).insertReservation(any(Reservation.class));
      verify(repository, times(1)).findTotalPriceById("aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
          "TEST");
    }
  }

  @Test
  void 宿泊プランの登録_リポジトリが呼ばれること() {
    HotelService sut = new HotelService(repository, converter);
    Booking booking = createBooking();

    sut.registerBooking(any(Authentication.class), booking);
    verify(repository, times(1))
        .insertBooking(booking);
  }

  @Test
  void 宿泊者情報の更新_リポジトリが呼ばれること() {
    HotelService sut = new HotelService(repository, converter);
    Authentication auth = getAuthentication();
    Guest guest = new Guest();
    guest.setName("山田太郎");

    sut.updateGuest(auth, guest);
    verify(repository, times(1)).updateGuest(guest, "TEST");
  }

  @Test
  void 宿泊プランの変更_リポジトリが呼ばれること() {
    HotelService sut = new HotelService(repository, converter);
    Authentication auth = getAuthentication();
    Reservation reservation = new Reservation();

    sut.updateReservation(auth, reservation);
    verify(repository, times(1)).updateReservation(reservation, "TEST");
  }

  @Test
  void 宿泊者の論理削除_リポジトリが呼び呼び出せていること() {
    HotelService sut = new HotelService(repository, converter);
    Authentication auth = getAuthentication();

    sut.logicalDeleteGuest(auth, "11111111-1111-1111-1111-111111111111");
    verify(repository, times(1))
        .toggleGuestDeletedFlag("11111111-1111-1111-1111-111111111111", "TEST");
  }

  @Nested
  @DisplayName("チェックイン処理の作成")
  class checkIn {

    @Test
    void チェックインが行われていること() {
      HotelService sut = new HotelService(repository, converter);
      Authentication auth = getAuthentication();

      when(repository.findStatusById("22222222-2222-2222-2222-222222222222", "TEST"))
          .thenReturn(ReservationStatus.NOT_CHECKED_IN);

      sut.checkIn(auth, "22222222-2222-2222-2222-222222222222");
      verify(repository, times(1)).checkIn(anyString(), anyString());

    }

    @Test
    void ステータスが未チェックインではない場合_エラーメッセージが表示される() {
      HotelService sut = new HotelService(repository, converter);
      Authentication auth = getAuthentication();

      when(repository.findStatusById("22222222-2222-2222-2222-222222222222", "TEST"))
          .thenReturn(ReservationStatus.CHECKED_IN);

      IllegalStateException ex = assertThrows(IllegalStateException.class,
          () -> sut.checkIn(auth, "22222222-2222-2222-2222-222222222222"));

      verify(repository, times(0)).checkIn("22222222-2222-2222-2222-222222222222", "TEST");
      verify(repository, times(1)).findStatusById("22222222-2222-2222-2222-222222222222", "TEST");
    }
  }

  @Nested
  @DisplayName("チェックアウト処理")
  class checkOut {

    @Test
    void チェックアウトが行われていること() {
      HotelService sut = new HotelService(repository, converter);
      Authentication auth = getAuthentication();

      when(repository.findStatusById("22222222-2222-2222-2222-222222222222", "TEST"))
          .thenReturn(ReservationStatus.CHECKED_IN);

      sut.checkOut(auth, "22222222-2222-2222-2222-222222222222");
      verify(repository, times(1)).checkOut(anyString(), anyString());
    }

    @Test
    void ステータスがチェックイン済みではない場合_エラーが発生する() {
      HotelService sut = new HotelService(repository, converter);
      Authentication auth = getAuthentication();

      when(repository.findStatusById("22222222-2222-2222-2222-222222222222", "TEST"))
          .thenReturn(ReservationStatus.CHECKED_OUT);

      IllegalStateException ex = assertThrows(IllegalStateException.class,
          () -> sut.checkOut(auth, "22222222-2222-2222-2222-222222222222"));

      assertEquals("チェックイン済みの予約のみチェックアウト可能です", ex.getMessage());
      verify(repository, times(0)).checkOut(anyString(), anyString());
      verify(repository, times(1)).findStatusById("22222222-2222-2222-2222-222222222222", "TEST");
    }
  }

  @Test
  void ユーザーの登録処理_リポジトリが呼び出せていること() {
    HotelService sut = new HotelService(repository, converter);
    User user = new User();
    user.setId("TEST");
    user.setPassword("HASHED");

    sut.registerUser(user);
    verify(repository, Mockito.times(1)).insertUser(user);
  }

  @Nested
  @DisplayName("ユーザーログインの処理")
  class loadUserByUsername {

    @Test
    void ユーザーが存在するとUserDetailsを返す() {
      HotelService sut = new HotelService(repository, converter);
      User user = new User();
      user.setId("TEST");
      user.setPassword("HASHED");

      when(repository.findUserById("TEST")).thenReturn(user);

      UserDetails actual = sut.loadUserByUsername("TEST");

      assertThat(actual.getUsername()).isEqualTo("TEST");
      assertThat(actual.getPassword()).isEqualTo("HASHED");
      assertThat(actual.getAuthorities())
          .extracting(GrantedAuthority::getAuthority)
          .containsExactly("ROLE_USER");

      verify(repository, times(1)).findUserById("TEST");
    }

    @Test
    void 見つからない場合はUsernameNotFoundException() {
      HotelService sut = new HotelService(repository, converter);
      when(repository.findUserById("UNKNOWN")).thenReturn(null);

      assertThatThrownBy(() -> sut.loadUserByUsername("UNKNOWN"))
          .isInstanceOf(UsernameNotFoundException.class)
          .hasMessage("ユーザーが見つかりません");

      verify(repository, times(1)).findUserById("UNKNOWN");
    }
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

  private static String getUserId(Authentication authentication) {
    return authentication.getName();
  }

  private Authentication getAuthentication() {
    return new UsernamePasswordAuthenticationToken("TEST", "pass",
        List.of(new SimpleGrantedAuthority("ROLE_USER")));
  }
}