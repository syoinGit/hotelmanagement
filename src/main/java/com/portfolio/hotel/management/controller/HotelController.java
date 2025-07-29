package com.portfolio.hotel.management.controller;

import com.portfolio.hotel.management.data.booking.Booking;
import com.portfolio.hotel.management.data.guest.Guest;
import com.portfolio.hotel.management.data.guest.GuestDetail;
import com.portfolio.hotel.management.data.guest.GuestMatch;
import com.portfolio.hotel.management.data.guest.GuestRegistration;
import com.portfolio.hotel.management.data.guest.GuestSearchCondition;
import com.portfolio.hotel.management.data.reservation.Reservation;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.time.LocalDate;
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
  public List<GuestDetail> getGuestList() {
    return service.getAllGuest();
  }

  @Operation(summary = "宿泊プラン一覧取得", description = "すべての宿泊プランを取得します。")
  @GetMapping("/getBookingList")
  public List<Booking> getAllBooking() {
    return service.getAllBooking();
  }

  @Operation(summary = "本日宿泊の宿泊予約を全件検索", description = "本日宿泊予定の宿泊予約を全件検索します")
  @GetMapping("/getCheckInToday")
  public List<GuestDetail> getChackInToday() {
    LocalDate today = LocalDate.now();
    return service.getChackInToday(today);
  }

  @Operation(summary = "本日退館の宿泊予約を全件検索", description = "本日退館予定の宿泊予約を取得いします")
  @GetMapping("/getCheckOutToday")
  public List<GuestDetail> getCheckOutToday() {
    LocalDate today = LocalDate.now();
    return service.getChackOutToday(today);
  }

  @Operation(summary = "単一検索", description = "ID、名前、ふりがな、電話番号、宿泊日から宿泊者情報を検索します。")
  @GetMapping("/searchGuest")
  public List<GuestDetail> searchGuest(@ModelAttribute GuestSearchCondition guestSearchCondition) {
    return service.searchGuest(guestSearchCondition);
  }

  @Operation(summary = "完全一致検索", description = "名前、ふりがな、電話番号から宿泊者情報を完全一致検索します。ここで完全位一致したデータは宿泊者情報登録の際に使われます")
  @PostMapping("/matchGuest")
  public GuestDetail matchGuestForInsert(@RequestBody @Valid GuestMatch guestMatch) {
    return service.matchGuest(guestMatch);
  }

  @Operation(summary = "宿泊者情報登録", description = "宿泊者情報を入力し、宿泊者情報を登録します。")
  @PutMapping("/registerGuest")
  public ResponseEntity<String> registerGuest(
      @RequestBody @Valid GuestRegistration guestRegistration) {
    service.registerGuest(guestRegistration);
    return ResponseEntity.ok("宿泊者情報の登録が完了しました。");
  }

  @Operation(summary = "宿泊プラン登録", description = "宿泊プランを入力し、登録します。")
  @PutMapping("/registerBooking")
  public ResponseEntity<String> registerBooking(@RequestBody @Valid Booking booking) {
    service.registerBooking(booking);
    return ResponseEntity.ok("宿泊プランの登録が完了しました。");
  }

  @Operation(summary = "宿泊者の変更", description = "宿泊者の変更を行います。")
  @PutMapping("/updateGuest")
  public ResponseEntity<String> updateGuest(@RequestBody Guest guest) {
    service.updateGuest(guest);
    return ResponseEntity.ok("宿泊者の変更が完了しました。");
  }

  @Operation(summary = "宿泊情報の変更", description = "宿泊情報の変更を行います。")
  @PutMapping("/updateReservation")
  public ResponseEntity<String> updateReservation(@RequestBody Reservation reservation) {
    service.updateReservation(reservation);
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