package com.portfolio.hotel.management.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.portfolio.hotel.management.data.guest.Guest;
import com.portfolio.hotel.management.service.HotelService;
import com.portfolio.hotel.management.data.guest.GuestDetail;
import com.portfolio.hotel.management.repository.HotelRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(HotelController.class)
class HotelControllerTest {

  @Autowired
  MockMvc mockMvc;

  @SuppressWarnings("removal")
  @MockBean
  private HotelService service;
  private HotelRepository repository;
  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void 宿泊者情報の全件検索_空のリストが帰ってくること() throws Exception {
    mockMvc.perform(get("/guestList")).andExpect(status().isOk()).andExpect(content().json("[]"));
    verify(service, times(1)).getAllGuest();
  }

  @Test
  void 本日チェックイン予定の宿泊者情報検索_空のリストが帰ってくること() throws Exception {
    LocalDate today = LocalDate.now();

    mockMvc.perform(get("/getCheckInToday")).andExpect(status().isOk())
        .andExpect(content().json("[]"));
    verify(service, times(1)).getChackInToday(, today, );
  }

  @Test
  void 本日チェックアウト予定の宿泊者検索_空のリストが帰ってくること() throws Exception {
    LocalDate today = LocalDate.now();

    mockMvc.perform(get("/getCheckOutToday")).andExpect(status().isOk())
        .andExpect(content().json("[]"));
    verify(service, times(1)).getChackOutToday(today);
  }

  @Test
  void 宿泊者情報の単一検索_宿泊者から宿泊者情報を検索できること() throws Exception {
    Guest guest = new Guest();
    guest.setName("佐藤花子");
    guest.setKanaName("サトウハナコ");
    guest.setPhone("08098765432");

    GuestDetail guestDetail = new GuestDetail();
    guestDetail.setGuest(guest);
    when(service.searchGuest(any(), )).thenReturn(List.of(guestDetail));

    mockMvc.perform(MockMvcRequestBuilders.get("/searchGuest")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                  {
                  "name": "佐藤花子",
                  "checkInDate": "2025-07-15"
                  }
                """
            ))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].guest.name").value("佐藤花子"));
  }

  @Test
  void 宿泊者情報の完全一致検索_名前_かな名_電話番号から宿泊者情報を検索できること()
      throws Exception {
    Guest guest = new Guest();
    guest.setGender("FEMALE");
    guest.setAge(28);

    GuestDetail guestDetail = new GuestDetail();
    guestDetail.setGuest(guest);

    when(service.matchGuest(, any())).thenReturn(guestDetail);

    mockMvc.perform(MockMvcRequestBuilders.post("/matchGuest")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                  {
                  "name": "佐藤花子",
                  "kanaName": "サトウハナコ",
                  "phone": "08098765432"
                }
                """
            ))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.guest.gender").value("FEMALE"))
        .andExpect(jsonPath("$.guest.age").value(28));
  }

  @Test
  void 宿泊者情報の登録_宿泊者情報が登録できること() throws Exception {
    mockMvc.perform(put("/registerGuest")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "guest": {
                    "name": "佐藤花子",
                    "kanaName": "サトウハナコ",
                    "gender": "FEMALE",
                    "age": 28,
                    "region": "東京",
                    "email": "hanako@example.com",
                    "phone": "08098765432"
                  },
                  "bookingId": "123e4567-e89b-12d3-a456-426614174000",
                  "stayDays": 2,
                  "checkInDate": "2025-07-31",
                  "memo": "観光で利用"
                }
                """))
        .andExpect(status().isOk())
        .andExpect(content().string("宿泊者情報の登録が完了しました。"));
  }

  @Test
  void 宿泊プランの登録_宿泊プランが登録できること() throws Exception {
    mockMvc.perform(put(("/registerBooking"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "name": "朝食付きプラン",
                    "description": "和洋朝食が選べるプラン",
                    "price": 10000,
                    "available": true
                }
                """)).andExpect(status().isOk())
        .andExpect(content().string("宿泊プランの登録が完了しました。"));
  }

  @Test
  void 宿泊者の変更_宿泊者が変更できているかの確認() throws Exception {
    mockMvc.perform(put("/updateGuest")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "guest": {
                    "name": "佐藤花子",
                    "kanaName": "サトウハナコ",
                    "gender": "FEMALE",
                    "age": 28,
                    "region": "東京",
                    "email": "hanako@example.com",
                    "phone": "08098765432"
                  }
                }
                """))
        .andExpect(status().isOk())
        .andExpect(content().string("宿泊者の更新が完了しました。"));
  }

  @Test
  void 宿泊情報の編集_宿泊情報が変更できているかの確認() throws Exception {

    mockMvc.perform(put("/updateReservation")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "id": "e1f7d3aa-4c59-4f9d-a8a1-8b9c3a6b91e7",
                  "guestId": "a23cde12-5c11-41f2-8bce-d43a7d00303f",
                  "bookingId": "b12def34-6c22-47e0-9ddc-abc123456789",
                  "checkInDate": "2025-08-15",
                  "stayDays": 2,
                  "totalPrice": 18000,
                  "status": "CHECKED_IN",
                  "memo": "フロントでチェックイン済み",
                  "createdAt": "2025-08-01T10:15:00"
                }
                """))
        .andExpect(status().isOk())
        .andExpect(content().string("宿泊情報の更新が完了しました。"));
  }

  @Test
  void 宿泊者の論理削除_削除完了のメッセージが返ってくること() throws Exception {
    String id = "dummy-id-123";
    String name = "山田";

    mockMvc.perform(put("/deleteGuest")
            .param("id", id)
            .param("name", name))
        .andExpect(status().isOk())
        .andExpect(content().string(name + "様の情報を削除しました。"));
  }

  @Test
  void チェックイン処理_チェックイン完了のメッセージが返ってくること() throws Exception {
    String id = "dummy-id-123";
    String name = "山田";

    mockMvc.perform(
            put("/checkIn")
                .param("id", id)
                .param("name", name))
        .andExpect(status().isOk())
        .andExpect(content().string(name + "様のチェックインが完了しました。"));
  }

  @Test
  void チェックアウト処理_チェックアウト完了のメッセージが帰ってくること() throws Exception {
    String name = "山田";
    String id = "dummy-id-123";

    mockMvc.perform(
            put("/checkOut")
                .param("id", id)
                .param("name", name))
        .andExpect(status().isOk())
        .andExpect(content().string(name + "様のチェックアウトが完了しました。"));
  }

  @Test
  void ユーザーの登録処理_登録完了のメッセージが返ってくるか確認() throws Exception {
    mockMvc.perform(put("/user/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                   "id": "testuser01",
                   "password": "testpass123"
                 }
                """))
        .andExpect(status().isOk())
        .andExpect(content().string("ユーザ情報の登録が完了しました。"));
  }
}