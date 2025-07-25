package com.portfolio.hotel.management.controller;

import com.portfolio.hotel.management.data.booking.Booking;
import com.portfolio.hotel.management.data.booking.BookingDto;
import com.portfolio.hotel.management.data.guest.Guest;
import com.portfolio.hotel.management.data.guest.GuestDetailDto;
import com.portfolio.hotel.management.data.guest.GuestDto;
import com.portfolio.hotel.management.data.guest.GuestRegistrationDto;
import com.portfolio.hotel.management.data.guest.GuestSearchDto;
import com.portfolio.hotel.management.data.reservation.Reservation;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.portfolio.hotel.management.service.HotelService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class HotelController {

  private final HotelService service;

  public HotelController(HotelService service) {
    this.service = service;
  }

  @Operation(summary = "全件検索", description = "宿泊者情報の全件検索を行います。")
  @GetMapping("/guestList")
  public List<GuestDetailDto> getGuestList() {
    return service.getAllGuest();
  }

  @Operation(summary = "宿泊プラン一覧取得", description = "すべての宿泊プランを取得します。")
  @GetMapping("/getBookingList")
  public List<BookingDto> getAllBooking() {
    return service.getAllBooking();
  }

  @Operation(summary = "本日宿泊の宿泊予約を全件検索", description = "本日宿泊予定の宿泊予約を全件検索します")
  @GetMapping("/getChackInToday")
  public List<GuestDetailDto> getChackInToday() {
    return service.getChackInToday();
  }


  @Operation(summary = "単一検索", description = "ID、名前、ふりがな、電話番号から宿泊者情報を検索します。")
  @GetMapping("/searchGuest")
  public List<GuestDetailDto> searchGuest(@ModelAttribute GuestDto guestDto) {
    return service.searchGuest(guestDto);
  }

  @Operation(summary = "完全一致検索", description = "名前、ふりがな、電話番号から宿泊者情報を完全一致検索します。ここで完全位一致したデータは宿泊者情報登録の際に使われます")
  @PostMapping("/matchGuest")
  public GuestDetailDto matchGuestForInsert(@RequestBody @Valid GuestSearchDto guestSearchDto) {
    return service.matchGuest(guestSearchDto);
  }

  @Operation(summary = "宿泊者情報登録", description = "宿泊者情報を入力し、宿泊者情報を登録します。")
  @PutMapping("/insertGuest")
  public ResponseEntity<String> registerBooking(
      @RequestBody @Valid GuestRegistrationDto guestRegistrationDto) {
    service.insertGuest(guestRegistrationDto);
    return ResponseEntity.ok("宿泊者情報の登録が完了しました。");
  }

  @Operation(summary = "宿泊プラン登録", description = "宿泊プランを入力し、登録します。")
  @PutMapping("/insertBooking")
  public ResponseEntity<String> registerBooking(@RequestBody @Valid Booking booking) {
    service.insertBooking(booking);
    return ResponseEntity.ok("宿泊プランの登録が完了しました。");
  }

  @Operation(summary = "宿泊者の変更", description = "宿泊者の変更を行います。")
  @PutMapping("/updateGuest")
  public ResponseEntity<String> editGuest(@RequestBody Guest guest) {
    service.editGuest(guest);
    return ResponseEntity.ok("宿泊者の変更が完了しました。");
  }

  @Operation(summary = "宿泊情報の変更", description = "宿泊情報の変更を行います。")
  @PutMapping("/updateReservation")
  public ResponseEntity<String> editReservation(@RequestBody Reservation reservation) {
    service.editReservation(reservation);
    return ResponseEntity.ok("宿泊情報の変更が完了しました。");
  }

  @Operation(summary = "チェックイン", description = "宿泊客のチェックインを行います。")
  @PutMapping("/checkIn")
  public ResponseEntity<String> checkIn(@RequestParam String reservationsId,
      @RequestParam String guestName) {
    service.checkIn(reservationsId);
    return ResponseEntity.ok(guestName + "様のチェックインが完了しました。");
  }

  @Operation(summary = "チェックアウト", description = "宿泊客のチェックアウトを行います。")
  @PutMapping("/checkOut")
  public ResponseEntity<String> checkOut(@RequestParam String reservationsId,
      @RequestParam String guestName) {
    service.checkOut(reservationsId);
    return ResponseEntity.ok(guestName + "様のチェックアウトが完了しました。");
  }
}