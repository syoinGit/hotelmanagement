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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.portfolio.hotel.management.repository.HotelRepository;
import com.portfolio.hotel.management.service.converter.HotelConverter;

@Service
public class HotelService implements UserDetailsService {

  private final HotelRepository repository;
  private final HotelConverter converter;

  public HotelService(HotelRepository repository, HotelConverter converter) {
    this.repository = repository;
    this.converter = converter;
  }

  // 宿泊者情報の全件取得
  public List<GuestDetail> getAllGuest(Authentication authentication) {
    String userId = extractLoginId(authentication);
    return converter.convertGuestDetail(
        repository.findAllGuest(userId),
        repository.findAllBooking(userId),
        repository.findAllReservation(userId));
  }

  // 本日チェックインの宿泊者を取得
  public List<GuestDetail> getChackInToday(Authentication authentication, LocalDate today) {
    String userId = extractLoginId(authentication);
    return converter.convertGuestDetail(
        repository.findGuestsTodayCheckIn(userId, today),
        repository.findAllBooking(userId),
        repository.findReservationTodayCheckIn(userId, today));
  }

  // 現在宿泊中の宿泊者を取得
  public List<GuestDetail> getStayNow(Authentication authentication) {
    String userId = extractLoginId(authentication);
    return converter.convertGuestDetail(
        repository.findGuestStayNow(userId),
        repository.findAllBooking(userId),
        repository.findReservationStayNow(userId));
  }

  // 本日チェックアウトの宿泊者を取得
  public List<GuestDetail> getChackOutToday(Authentication authentication, LocalDate today) {
    String userId = extractLoginId(authentication);
    return converter.convertGuestDetail(
        repository.findGuestsTodayCheckOut(userId, today),
        repository.findAllBooking(userId),
        repository.findReservationTodayCheckOut(userId, today));
  }

  // 宿泊コースの全件取得
  public List<Booking> getAllBooking(Authentication authentication) {
    String userId = extractLoginId(authentication);
    return repository.findAllBooking(userId);
  }

  // 宿泊者情報の単一検索
  public List<GuestDetail> searchGuest(Authentication authentication,
      GuestSearchCondition guestSearchCondition) {
    String userId = extractLoginId(authentication);
    guestSearchCondition.setUserId(userId);

    return converter.convertGuestDetail(
        repository.searchGuest(guestSearchCondition),
        repository.findAllBooking(userId),
        repository.findAllReservation(userId));
  }

  // 宿泊者の完全一致検索
  public GuestRegistration matchGuest(Authentication authentication, GuestMatch guestMatch) {
    guestMatch.setUserId(extractLoginId(authentication));
    Guest guest = repository.matchGuest(guestMatch);

    GuestRegistration guestRegistration = new GuestRegistration();
    if (guest != null) {
      guestRegistration.setGuest(guest);
    } else {
      guestRegistration.setGuest(converter.toGuest(guestMatch));
    }

    return guestRegistration;
  }

  // 宿泊者の登録
  public void registerGuest(Authentication authentication, GuestRegistration guestRegistration) {
    guestRegistration.getGuest().setUserId(extractLoginId(authentication));
    // 直前の検索で一致する宿泊者がなかった場合新規登録
    if (guestRegistration.getGuest().getId() == null) {
      guestRegistration.getGuest().setId(UUID.randomUUID().toString());
      repository.insertGuest(guestRegistration.getGuest());
    }
    initReservation(guestRegistration);
  }

  // 宿泊予約の登録
  private void initReservation(GuestRegistration guestRegistration) {
    final String userId = guestRegistration.getGuest().getUserId();

    Reservation reservation = new Reservation();

    reservation.setId(UUID.randomUUID().toString());
    reservation.setUserId(guestRegistration.getGuest().getUserId());
    reservation.setGuestId(guestRegistration.getGuest().getId());
    reservation.setBookingId(guestRegistration.getBookingId());
    reservation.setCheckInDate(guestRegistration.getCheckInDate());
    reservation.setStayDays(guestRegistration.getStayDays());
    reservation.setCheckOutDate(
        reservation.getCheckInDate().plusDays(guestRegistration.getStayDays()));
    BigDecimal price = repository.findTotalPriceById(reservation.getBookingId(),
        guestRegistration.getGuest().getUserId());
    BigDecimal total = price.multiply(BigDecimal.valueOf(reservation.getStayDays()));
    reservation.setTotalPrice(total);
    reservation.setMemo(guestRegistration.getMemo());
    reservation.setStatus(ReservationStatus.NOT_CHECKED_IN);
    repository.insertReservation(reservation);
  }

  // 宿泊プランの登録
  public void registerBooking(Booking booking) {
    booking.setId(UUID.randomUUID().toString());
    repository.insertBooking(booking);
  }

  // 宿泊者の編集
  public void updateGuest(Authentication authentication, Guest guest) {
    repository.updateGuest(guest, extractLoginId(authentication));
  }

  // 宿泊予約の編集
  public void updateReservation(Authentication authentication, Reservation reservation) {
    repository.updateReservation(reservation, extractLoginId(authentication));
  }

  // 宿泊者の削除
  public void logicalDeleteGuest(Authentication authentication, String guestId) {
    repository.logicalDeleteGuest(guestId, extractLoginId(authentication));
  }

  // チェックイン処理
  public void checkIn(Authentication authentication, String id) {
    ReservationStatus status = repository.findStatusById(id, extractLoginId(authentication));
    if (status == ReservationStatus.NOT_CHECKED_IN) {
      repository.checkIn(id, extractLoginId(authentication));
    } else {
      throw new IllegalStateException("未チェックインの予約のみチェックイン可能です");
    }
  }

  // チェックアウト処理
  public void checkOut(Authentication authentication, String id) {
    ReservationStatus status = repository.findStatusById(id, extractLoginId(authentication));
    if (status == ReservationStatus.CHECKED_IN) {
      repository.checkOut(id, extractLoginId(authentication));
    } else {
      throw new IllegalStateException("チェックイン済みの予約のみチェックアウト可能です");
    }
  }

  // 新規ユーザの登録
  public void registerUser(User user) {
    repository.insertUser(user);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User found = repository.findUserById(username);
    if (found == null) {
      throw new UsernameNotFoundException("ユーザーが見つかりません");
    }
    return new org.springframework.security.core.userdetails.User(
        found.getId(),
        found.getPassword(),
        List.of(new SimpleGrantedAuthority("ROLE_USER"))
    );
  }

  private static String extractLoginId(Authentication authentication) {
    return authentication.getName();
  }
}