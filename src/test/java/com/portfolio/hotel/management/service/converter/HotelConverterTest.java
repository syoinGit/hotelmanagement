package com.portfolio.hotel.management.service.converter;

import static org.assertj.core.api.Assertions.assertThat;

import com.portfolio.hotel.management.data.booking.BookingDto;
import com.portfolio.hotel.management.data.guest.GuestDetailDto;
import com.portfolio.hotel.management.data.guest.GuestDto;
import com.portfolio.hotel.management.data.guest.GuestSearchDto;
import com.portfolio.hotel.management.data.reservation.ReservationDto;
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
    GuestDto guestDto = getGuest();
    guestDto.setId("11111111-1111-1111-1111-111111111111");

    BookingDto bookingDto = getBooking();
    bookingDto.setId("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    ReservationDto reservationDto = getReservationDto();

    List<GuestDto> guestDtoList = List.of(guestDto);
    List<BookingDto> bookingDtoList = List.of(bookingDto);
    List<ReservationDto> reservationDtoList = List.of(reservationDto);

    List<GuestDetailDto> actual = sut.convertGuestDetailDto(guestDtoList, bookingDtoList,
        reservationDtoList);

    GuestDetailDto result = new GuestDetailDto();
    result.setGuest(actual.getFirst().getGuest());
    result.setBookings(actual.getFirst().getBookings());
    result.setReservations(actual.getFirst().getReservations());

    assertThat(result.getGuest().getName()).isEqualTo("佐藤花子");
    assertThat(result.getReservations().getFirst().getMemo()).isEqualTo("観光利用");

  }

  @Test
  void コンバーターを使用して_宿泊者検索を宿泊者に変換できる() {
    GuestSearchDto guestSearchDto = new GuestSearchDto();
    guestSearchDto.setName("佐藤花子");
    guestSearchDto.setKanaName("サトウハナコ");
    guestSearchDto.setPhone("08098765432");

    GuestDto actual = sut.toGuestDto(guestSearchDto);

    assertThat(actual.getName()).isEqualTo("佐藤花子");
    assertThat(actual.getKanaName()).isEqualTo("サトウハナコ");

  }

  private GuestDto getGuest() {
    GuestDto guestDto = new GuestDto();

    guestDto.setName("佐藤花子");
    guestDto.setKanaName("サトウハナコ");
    guestDto.setGender("FEMALE");
    guestDto.setAge(28);
    guestDto.setRegion("東京");
    guestDto.setEmail("hanako@example.com");
    guestDto.setPhone("08098765432");
    return guestDto;
  }

  private BookingDto getBooking() {
    BookingDto bookingDto = new BookingDto();
    bookingDto.setName("和洋朝食付きプラン");
    bookingDto.setDescription("和食・洋食が選べる朝食付きのプランです。");
    bookingDto.setPrice(BigDecimal.valueOf(1000));
    return bookingDto;
  }

  private ReservationDto getReservationDto() {
    ReservationDto reservationDto = new ReservationDto();
    reservationDto.setGuestId("11111111-1111-1111-1111-111111111111");
    reservationDto.setBookingId("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    reservationDto.setCheckInDate(LocalDate.of(2025, 7, 22));
    reservationDto.setStayDays(2);
    reservationDto.setTotalPrice(BigDecimal.valueOf(1000));
    reservationDto.setStatus(
        ReservationStatus.NOT_CHECKED_IN); // enum であれば ReservationStatus.NOT_CHECKED_IN など
    reservationDto.setMemo("観光利用");
    reservationDto.setCreatedAt(LocalDateTime.now());
    return reservationDto;
  }
}