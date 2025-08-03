package com.portfolio.hotel.management.service;

import com.portfolio.hotel.management.data.booking.Booking;
import com.portfolio.hotel.management.data.guest.Guest;
import com.portfolio.hotel.management.data.guest.GuestDetail;
import com.portfolio.hotel.management.data.guest.GuestMatch;
import com.portfolio.hotel.management.data.guest.GuestRegistration;
import com.portfolio.hotel.management.data.guest.GuestSearchCondition;
import com.portfolio.hotel.management.data.reservation.Reservation;
import com.portfolio.hotel.management.data.reservation.ReservationStatus;
import com.portfolio.hotel.management.data.user.User;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.portfolio.hotel.management.repository.HotelRepository;
import com.portfolio.hotel.management.service.converter.HotelConverter;

@Service
public class HotelService {

  private final HotelRepository repository;
  private final HotelConverter converter;

  public HotelService(HotelRepository repository, HotelConverter converter) {
    this.repository = repository;
    this.converter = converter;
  }

  // 宿泊者情報の全件取得
  public List<GuestDetail> getAllGuest(HttpSession session) {
    String userId = (String) session.getAttribute("userId");

    return converter.convertGuestDetail(
        repository.findAllGuest(userId),
        repository.findAllBooking(userId),
        repository.findAllReservation(userId));
  }

  // 本日チェックインの宿泊者を取得
  public List<GuestDetail> getChackInToday(HttpSession session, LocalDate today) {
    String userId = (String) session.getAttribute("userId");

    return converter.convertGuestDetail(
        repository.findGuestsTodayCheckIn(today),
        repository.findAllBooking(userId),
        repository.findReservationTodayCheckIn(today));
  }

  // 本日チェックアウトの宿泊者を取得
  public List<GuestDetail> getChackOutToday(HttpSession session, LocalDate today) {
    String userId = (String) session.getAttribute("userId");

    return converter.convertGuestDetail(
        repository.findGuestsTodayCheckOut(today),
        repository.findAllBooking(userId),
        repository.findReservationTodayCheckOut(today));
  }



  // 宿泊コースの全件取得
  public List<Booking> getAllBooking(HttpSession session) {
    String userId = (String) session.getAttribute("userId");
    return repository.findAllBooking(userId);
  }

  // 宿泊者情報の単一検索
  public List<GuestDetail> searchGuest(GuestSearchCondition guestSearchCondition, HttpSession session) {
    String userId = (String) session.getAttribute("userId");

    return converter.convertGuestDetail(
        repository.searchGuest(guestSearchCondition),
        repository.findAllBooking(userId),
        repository.findAllReservation(userId));
  }

  // 宿泊者の完全一致検索
  public GuestDetail matchGuest(GuestMatch guestMatch) {
    Guest guest = repository.matchGuest(guestMatch);
    GuestDetail guestDetail = new GuestDetail();
    // 一致するものがなかった場合、guestの数値を入れる。
    if (guest == null) {
      guestDetail.setGuest(converter.toGuest(guestMatch));
      // 一致した場合、取得したguestを入れる。
    } else {
      guestDetail.setGuest(guest);
    }
    return guestDetail;
  }

  // 宿泊者の登録
  public void registerGuest(GuestRegistration guestRegistration) {
    // 直前の検索で一致する宿泊者がなかった場合新規登録
    if (guestRegistration.getGuest().getId() == null) {
      guestRegistration.getGuest().setId(UUID.randomUUID().toString());
      repository.insertGuest(guestRegistration.getGuest());
    }
    initReservation(guestRegistration);
  }

  // 宿泊予約の登録
  private void initReservation(GuestRegistration guestRegistration) {
    Reservation reservation = new Reservation();

    reservation.setId(UUID.randomUUID().toString());
    reservation.setGuestId(guestRegistration.getGuest().getId());
    reservation.setBookingId(guestRegistration.getBookingId());
    reservation.setCheckInDate(guestRegistration.getCheckInDate());
    reservation.setStayDays(guestRegistration.getStayDays());
    reservation.setCheckOutDate(
        reservation.getCheckInDate().plusDays(guestRegistration.getStayDays()));
    BigDecimal price = repository.findTotalPriceById(reservation.getBookingId());
    BigDecimal total = price.multiply(BigDecimal.valueOf(reservation.getStayDays()));
    reservation.setTotalPrice(total);
    reservation.setMemo(guestRegistration.getMemo());
    reservation.setStatus(ReservationStatus.NOT_CHECKED_IN);
    reservation.setCheckInDate(LocalDate.now());

    repository.insertReservation(reservation);
  }

  // 宿泊プランの登録
  public void registerBooking(Booking booking) {
    booking.setId(UUID.randomUUID().toString());
    repository.insertBooking(booking);
  }

  // 宿泊者の編集
  public void updateGuest(Guest guest) {
    repository.updateGuest(guest);
  }

  // 宿泊予約の編集
  public void updateReservation(Reservation reservation) {
    repository.updateReservation(reservation);
  }

  // チェックイン処理の作成
  public void checkIn(String reservationId) {
    ReservationStatus status = repository.findStatusById(reservationId);
    if (status == ReservationStatus.NOT_CHECKED_IN) {
      repository.checkIn(reservationId);
    } else {
      throw new IllegalStateException("未チェックインの予約のみチェックイン可能です");
    }
  }

  // チェックアウト処理の作成
  public void checkOut(String reservationId) {
    ReservationStatus status = repository.findStatusById(reservationId);
    if (status == ReservationStatus.CHECKED_IN) {
      repository.checkOut(reservationId);
    } else {
      throw new IllegalStateException("チェックイン済みの予約のみチェックアウト可能です");
    }
  }

  // 新規ユーザの登録
  public void registerUser(User user) {
    repository.insertUser(user);
  }

  // ログイン処理
  public ResponseEntity<String> login(User user, HttpSession session) {
    User found = repository.findUserById(user.getId());
    if (found != null && found.getPassword().equals(user.getPassword())) {
      session.setAttribute("loginUser", found);
      session.setAttribute("userId", found.getId());
      return ResponseEntity.ok("ログインしました。");
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("パスワードが違います。");
    }
  }
}