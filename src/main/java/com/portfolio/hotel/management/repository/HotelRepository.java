package com.portfolio.hotel.management.repository;

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
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HotelRepository {

  // 宿泊者の全件検索
  List<Guest> findAllGuest(@Param("id") String id);

  // 宿泊プランの全件検索
  List<Booking> findAllBooking(@Param("id") String id);

  // 宿泊予約の全件検索
  List<Reservation> findAllReservation(@Param("id") String id);

  // 本日チェックイン予定の宿泊者を検索
  List<Guest> findGuestsTodayCheckIn(@Param("today") LocalDate today);

  // 本日チェックイン予定の宿泊予約を検索
  List<Reservation> findReservationTodayCheckIn(@Param("today") LocalDate today);

  // 本日チェックアウト予定の宿泊者情報を検索
  List<Guest> findGuestsTodayCheckOut(@Param("today") LocalDate today);

  // 本日チェックアウト予定の宿泊予約を検索
  List<Reservation> findReservationTodayCheckOut(@Param("today") LocalDate today);

  // 宿泊者ID、名前、かな名、電話番号から宿泊者を検索
  List<Guest> searchGuest(@Param("guest") GuestSearchCondition guestSearchCondition);

  // 宿泊予約IDから、宿泊予約を検索
  Reservation searchReservation(@Param("id") String id);

  // 宿泊者IDから宿泊者を完全一致検索
  Guest matchGuest(GuestMatch guestMatch);

  // 宿泊プランIDから金額を検索
  BigDecimal findTotalPriceById(@Param("id") String id);

  // 宿泊予約IDから宿泊予約状況を検索
  ReservationStatus findStatusById(@Param("id") String id);

  //　ユーザーIDからユーザー情報を検索
  User findUserById(@Param("id") String id);

  // 宿泊者の登録
  void insertGuest(Guest guest);

  // 宿泊プランの登録
  void insertBooking(Booking booking);

  // 宿泊予約の登録
  void insertReservation(Reservation reservation);

  // 宿泊者情報の変更
  void updateGuest(Guest guest);

  // 宿泊予約の変更
  void updateReservation(Reservation reservation);

  // チェックイン処理
  void checkIn(@Param("id") String id);

  // チェックアウト処理
  void checkOut(@Param("id") String id);

  // ユーザーの登録処理
  void insertUser(User user);

}