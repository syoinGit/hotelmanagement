package com.portfolio.hotel.management.controller;

import com.portfolio.hotel.management.data.booking.Booking;
import com.portfolio.hotel.management.data.guest.Guest;
import com.portfolio.hotel.management.data.guest.GuestDetail;
import com.portfolio.hotel.management.data.guest.GuestMatch;
import com.portfolio.hotel.management.data.guest.GuestRegistration;
import com.portfolio.hotel.management.data.guest.GuestSearchCondition;
import com.portfolio.hotel.management.data.reservation.Reservation;
import com.portfolio.hotel.management.data.user.User;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.portfolio.hotel.management.service.HotelService;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
public class HotelController {

  private final HotelService service;

  public HotelController(HotelService service) {
    this.service = service;
  }

  @Operation(summary = "全件検索", description = "宿泊者情報の全件検索を行います。")
  @GetMapping("/guests")
  public List<GuestDetail> getGuestList(Authentication authentication) {
    return service.getAllGuest(authentication);
  }

  @Operation(summary = "宿泊プラン一覧取得", description = "すべての宿泊プランを取得します。")
  @GetMapping("/bookings")
  public List<Booking> getAllBooking(Authentication authentication) {
    return service.getAllBooking(authentication);
  }

  @Operation(summary = "本日宿泊の宿泊者を全件検索", description = "本日宿泊予定の宿泊予約を全件検索します")
  @GetMapping("/guests/check-in-today")
  public List<GuestDetail> getChackInToday(Authentication authentication) {
    LocalDate today = LocalDate.now();
    return service.getChackInToday(authentication, today);
  }

  @Operation(summary = "現在宿泊中の宿泊者を全件検索", description = "現在宿泊中の宿泊者を全件検索します")
  @GetMapping("/guests/stay")
  public List<GuestDetail> getStay(Authentication authentication) {
    return service.getStayNow(authentication);
  }

  @Operation(summary = "本日退館の宿泊者を全件検索", description = "本日退館予定の宿泊者を全件検索します")
  @GetMapping("/guests/check-out-today")
  public List<GuestDetail> getCheckOutToday(Authentication authentication) {
    LocalDate today = LocalDate.now();
    return service.getChackOutToday(authentication, today);
  }

  @Operation(summary = "単一検索", description = "ID、名前、ふりがな、電話番号、宿泊日から宿泊者情報を検索します。")
  @PostMapping("/guest/search")
  public List<GuestDetail> searchGuest(Authentication authentication,
      @RequestBody GuestSearchCondition guestSearchCondition) {
    return service.searchGuest(authentication, guestSearchCondition);
  }

  @Operation(summary = "完全一致検索", description = "名前、ふりがな、電話番号から宿泊者情報を完全一致検索します。ここで完全位一致したデータは宿泊者情報登録の際に使われます")
  @PostMapping("/guest/match")
  public GuestRegistration matchGuestForInsert(Authentication authentication,
      @RequestBody @Valid GuestMatch guestMatch) {
    return service.matchGuest(authentication,guestMatch);
  }

  @Operation(summary = "宿泊者情報登録", description = "宿泊者情報を入力し、宿泊者情報を登録します。")
  @PutMapping("/guest/register")
  public ResponseEntity<String> registerGuest(Authentication authentication,
      @RequestBody @Valid GuestRegistration guestRegistration) {
    service.registerGuest(authentication, guestRegistration);
    return ResponseEntity.ok("宿泊者情報の登録が完了しました。");
  }

  @Operation(summary = "宿泊プラン登録", description = "宿泊プランを入力し、登録します。")
  @PutMapping("/registerBooking")
  public ResponseEntity<String> registerBooking(@RequestBody @Valid Booking booking) {
    service.registerBooking(booking);
    return ResponseEntity.ok("宿泊プランの登録が完了しました。");
  }
  
@Operation(summary = "宿泊者の更新", description = "宿泊者の情報を更新します。")
@PutMapping("/guest/update")
public ResponseEntity<String> updateGuest(@RequestBody Guest guest) {
  service.updateGuest(guest);
  return ResponseEntity.ok("宿泊者の更新が完了しました。");
}

@Operation(summary = "宿泊情報の更新", description = "宿泊予約情報を更新します。")
@PutMapping("/reservation/update")
public ResponseEntity<String> updateReservation(@RequestBody Reservation reservation) {
  System.out.println("受け取ったReservation: " + reservation);
  service.updateReservation(reservation);
  return ResponseEntity.ok("宿泊情報の更新が完了しました。");
}

@Operation(summary = "宿泊者の論理削除", description = "宿泊者の削除フラグをtrueにします。")
@PutMapping("/deleteGuest")
public ResponseEntity<String> logicalDeleteGuest(
    @RequestParam String id,
    @RequestParam String name) {
  service.logicalDeleteGuest(id);
  return ResponseEntity.ok(name + "様の情報を削除しました。");
}

@Operation(summary = "チェックイン", description = "宿泊客のチェックインを行います。")
@PutMapping("/guest/checkIn")
public ResponseEntity<String> checkIn(
    @RequestParam String id,
    @RequestParam String name) {
  service.checkIn(id);
  return ResponseEntity.ok(name + "様のチェックインが完了しました。");
}

  @Operation(summary = "チェックアウト", description = "宿泊客のチェックアウトを行います。")
  @PutMapping("/checkOut")
  public ResponseEntity<String> checkOut(
      @RequestParam String id,
      @RequestParam String name) {
    service.checkOut(id);
    return ResponseEntity.ok(name + "様のチェックアウトが完了しました。");
  }

  @Operation(summary = "新規ユーザーの登録", description = "IDとパスワードを取得して、新規ユーザの登録を行います。")
  @PutMapping("/user/register")
  public ResponseEntity<String> registerUser(@RequestBody User user) {
    service.registerUser(user);
    return ResponseEntity.ok("ユーザ情報の登録が完了しました。");
  }
}