package com.portfolio.hotel.management.service;

import com.portfolio.hotel.management.data.booking.Booking;
import com.portfolio.hotel.management.data.booking.BookingDto;
import com.portfolio.hotel.management.data.guest.Guest;
import com.portfolio.hotel.management.data.guest.GuestDetailDto;
import com.portfolio.hotel.management.data.guest.GuestDto;
import com.portfolio.hotel.management.data.guest.GuestRegistrationDto;
import com.portfolio.hotel.management.data.guest.GuestSearchDto;
import com.portfolio.hotel.management.data.reservation.Reservation;
import com.portfolio.hotel.management.data.reservation.ReservationDto;
import com.portfolio.hotel.management.data.reservation.ReservationStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
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
  public List<GuestDetailDto> getAllGuest() {
    return converter.convertGuestDetailDto(
        repository.findAllGuest(),
        repository.findAllBooking(),
        repository.findAllReservation());
  }

  // 本日チェックインの宿泊者を取得
  public List<GuestDetailDto> getChackInToday() {

    List<GuestDto> guestDtoList = repository.findGuestsTodayCheckIn();
    List<ReservationDto> reservationDtoList = repository.findReservationTodayCheckIn();

    return converter.convertGuestDetailDto(repository.findGuestsTodayCheckIn(),
        repository.findAllBooking(), repository.findReservationTodayCheckIn());
  }

  // 宿泊コースの全件取得
  public List<BookingDto> getAllBooking() {
    return repository.findAllBooking();
  }


  // 宿泊者情報の単一検索
  public List<GuestDetailDto> searchGuest(GuestDto guestDto) {
    return converter.convertGuestDetailDto(
        repository.searchGuest(guestDto),
        repository.findAllBooking(),
        repository.findAllReservation());
  }

  // 宿泊者の完全一致検索
  public GuestDetailDto matchGuest(GuestSearchDto guestSearchDto) {
    GuestDto guestDto = repository.matchGuest(guestSearchDto);
    GuestDetailDto dto = new GuestDetailDto();
    // 一致するものがなかった場合、guestの数値を入れる。
    if (guestDto == null) {
      dto.setGuest(converter.toGuestDto(guestSearchDto));
      // 一致した場合、取得したguestDtoを入れる。
    } else {
      dto.setGuest(guestDto);
    }
    return dto;
  }

  // ゲスト情報の登録
  public void insertGuest(GuestRegistrationDto guestRegistrationDto) {
    // 直前の検索で一致する宿泊者がなかった場合新規登録
    if (guestRegistrationDto.getGuest().getId() == null) {
      guestRegistrationDto.getGuest().setId(UUID.randomUUID().toString());
      repository.insertGuest(guestRegistrationDto.getGuest());
    }
    initReservation(guestRegistrationDto);
  }

  // 宿泊予約の登録
  private void initReservation(GuestRegistrationDto guestRegistrationDto) {
    ReservationDto dto = new ReservationDto();

    dto.setId(UUID.randomUUID().toString());
    dto.setGuestId(guestRegistrationDto.getGuest().getId());
    dto.setBookingId(guestRegistrationDto.getBookingId());
    dto.setCheckInDate(guestRegistrationDto.getCheckInDate());
    dto.setStayDays(guestRegistrationDto.getStayDays());

    BigDecimal price = repository.findTotalPriceById(dto.getBookingId());
    BigDecimal total = price.multiply(BigDecimal.valueOf(dto.getStayDays()));
    dto.setTotalPrice(total);
    dto.setMemo(guestRegistrationDto.getMemo());
    dto.setStatus(ReservationStatus.TEMPORARY);
    dto.setCheckInDate(LocalDate.now());

    repository.insertReservation(dto);
  }

  // 宿泊プランの登録
  public void insertBooking(Booking booking) {
    booking.setId(UUID.randomUUID().toString());
    repository.insertBooking(booking);
  }

  // 宿泊者の編集
  public void editGuest(Guest guest) {
    repository.editGuest(guest);
  }

  // 宿泊予約の編集
  public void editReservation(Reservation reservation) {
    repository.editReservation(reservation);
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
    repository.checkOut(reservationId);
    ReservationStatus status = repository.findStatusById(reservationId);
    if (status == ReservationStatus.CHECKED_IN) {
      repository.checkOut(reservationId);
    } else {
      throw new IllegalStateException("チェックイン済みの予約のみチェックアウト可能です");
    }
  }
}