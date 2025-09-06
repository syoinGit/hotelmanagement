package com.portfolio.hotel.management.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;


@MybatisTest
class HotelRepositoryTest {

  @Autowired
  HotelRepository sut;


  @Nested
  @DisplayName("宿泊者の全件検索")
  class FindAllGuest {

    @Test
    void 登録された3件の宿泊者が取得できる() {
      List<Guest> actual = sut.findAllGuest("testuser01");

      assertThat(actual)
          .extracting(Guest::getName)
          .containsExactlyInAnyOrder("佐藤花子", "田中太郎","山田毅");
    }

    @Test
    void ユーザーIDが一致しなかった場合_空のリストが返る() {
      List<Guest> actual = sut.findAllGuest("not-exist");
      assertThat(actual).isEmpty();
    }
  }


  @Nested
  @DisplayName("宿泊プランの全件検索")
  class findAllBooking {

    @Test
    void 登録された二件の宿泊プランが取得できる() {
      List<Booking> actual = sut.findAllBooking("testuser01");

      assertThat(actual)
          .extracting(Booking::getName)
          .containsExactlyInAnyOrder("朝食付きプラン", "素泊まりプラン");
    }

    @Test
    void 一致するIDがなかった場合_空のリストが返る() {
      List<Booking> actual = sut.findAllBooking("not-exist");
      assertThat(actual).isEmpty();
    }
  }


  @Nested
  @DisplayName("宿泊予約の全件検索")
  class findAllReservation {

    @Test
    void 登録された二件の宿泊予約が取得できる() {
      List<Reservation> actual = sut.findAllReservation("testuser01");

      assertThat(actual)
          .extracting(Reservation::getId)
          .containsExactlyInAnyOrder("rsv00001-aaaa-bbbb-cccc-000000000001",
              "rsv00002-bbbb-cccc-dddd-000000000002","rsv00002-bbbb-cccc-dddd-000000000003");
    }
  }

  @Test
  void 一致するIDがなかった場合_空のリストが返る() {
    List<Reservation> actual = sut.findAllReservation("not-exist");
    assertThat(actual).isEmpty();
  }


  @Nested
  @DisplayName("本日チェックイン予定の宿泊者を検索")
  class findGuestsTodayCheckIn {

    @Test
    void 登録された一件の宿泊者を取得できる() {
      List<Guest> actual = sut.findGuestsTodayCheckIn(
          "testuser01", LocalDate.of(2025, 7, 24));

      assertThat(actual)
          .extracting(Guest::getName)
          .containsExactlyInAnyOrder("田中太郎");
    }

    @Test
    void 日付が一致しない場合_空のリストが返る() {
      List<Guest> actual = sut.findGuestsTodayCheckIn(
          "testuser01", LocalDate.of(2020, 1, 1));

      assertThat(actual).isEmpty();
    }

    @Test
    void ユーザ名が一致しない場合_空のリストが返る() {
      var actual = sut.findGuestsTodayCheckIn(
          "not-exist", LocalDate.of(2025, 7, 24));

      assertThat(actual).isEmpty();
    }
  }


  @Nested
  @DisplayName("本日チェックイン予定の宿泊予約を検索")
  class findReservationTodayCheckIn {

    @Test
    void 登録された一件の宿泊予約を取得できる() {
      List<Reservation> actual = sut.findReservationTodayCheckIn(
          "testuser01", LocalDate.of(2025, 7, 24));

      assertThat(actual)
          .extracting(Reservation::getId)
          .containsExactlyInAnyOrder("rsv00002-bbbb-cccc-dddd-000000000002");
    }

    @Test
    void 日付が一致しない場合_空のリストが返る() {
      List<Reservation> actual = sut.findReservationTodayCheckIn(
          "testuser01", LocalDate.of(2020, 1, 1));

      assertThat(actual).isEmpty();
    }

    @Test
    void ユーザ名が一致しない場合_空のリストが返る() {
      List<Reservation> actual = sut.findReservationTodayCheckIn(
          "not-exist", LocalDate.of(2025, 7, 24));

      assertThat(actual).isEmpty();
    }
  }


  @Nested
  @DisplayName("現在宿泊中の宿泊者を検索")
  class findGuestStayNow {

    @Test
    void 宿泊中の宿泊者一件が取得できる() {
      List<Guest> actual = sut.findGuestStayNow("testuser01");
      assertThat(actual)
          .extracting(Guest::getName)
          .containsExactlyInAnyOrder("佐藤花子");
    }

    @Test
    void ユーザ名が一致しない場合_空のリストが返る() {
      List<Guest> actual = sut.findGuestStayNow("not-exist");
      assertThat(actual).isEmpty();
    }

    @Test
    void ステータスがチェックイン中ではない場合_空のリストが返る() {
      List<Guest> actual = sut.findGuestStayNow("status-test");
      assertThat(actual).isEmpty();
    }
  }


  @Nested
  @DisplayName("現在宿泊中の宿泊予約を検索")
  class findReservationStayNow {

    @Test
    void 宿泊中の宿泊予約が一件取得できる() {
      List<Reservation> actual = sut.findReservationStayNow("testuser01");
      assertThat(actual)
          .extracting(Reservation::getId)
          .containsExactlyInAnyOrder("rsv00001-aaaa-bbbb-cccc-000000000001");
    }

    @Test
    void ステータスがチェックイン中ではない場合_空のリストが返る() {
      List<Reservation> actual = sut.findReservationStayNow("statusTest");
      assertThat(actual).isEmpty();
    }
  }


  @Nested
  @DisplayName("本日チェックアウト予定の宿泊者情報を検索")
  class findGuestsTodayCheckOut {

    @Test
    void チェックアウト予定の宿泊者一件の宿泊者が取得できる() {
      List<Guest> actual = sut.findGuestsTodayCheckOut(
          "testuser01", LocalDate.of(2025, 7, 27));

      assertThat(actual)
          .extracting(Guest::getName)
          .containsExactlyInAnyOrder("佐藤花子");
    }

    @Test
    void 本日チェックアウト予定の宿泊者がいない場合_空のリストが返る() {
      List<Guest> actual = sut.findGuestsTodayCheckOut(
          getUserId(), LocalDate.of(2020, 7, 27));
    }

    @Test
    void ユーザ名が一致しない場合_空のリストが返る() {
      List<Guest> actual = sut.findGuestsTodayCheckOut(
          "not-exist", LocalDate.of(2025, 7, 27));

      assertThat(actual).isEmpty();
    }
  }


  @Nested
  @DisplayName("本日チェックアウト予定の宿泊予約を検索")
  class findReservationsTodayCheckOut {

    @Test
    void チェックアウト予定の宿泊予約一件が取得できる() {
      List<Reservation> actual = sut.findReservationTodayCheckOut(
          "testuser01", LocalDate.of(2025, 7, 27));

      assertThat(actual)
          .extracting(Reservation::getId)
          .containsExactlyInAnyOrder("rsv00001-aaaa-bbbb-cccc-000000000001");
    }

    @Test
    void 本日チェックアウト予定の宿泊予約がない場合_空のリストが返る() {
      List<Reservation> actual = sut.findReservationTodayCheckOut(
          "testuser01", LocalDate.of(2020, 7, 27));

      assertThat(actual).isEmpty();
    }

    @Test
    void ユーザ名が一致しない場合_空のリストが返る() {
      List<Reservation> actual = sut.findReservationTodayCheckOut(
          "not-exist", LocalDate.of(2025, 7, 27));

      assertThat(actual).isEmpty();
    }
  }


  @Nested
  @DisplayName("宿泊者の単一検索")
  class searchGuest {

    @Test
    void 名前の部分一致で宿泊者が取得できる() {
      GuestSearchCondition guestSearchCondition = new GuestSearchCondition();
      guestSearchCondition.setName("花子");
      guestSearchCondition.setUserId("testuser01");

      List<Guest> actual = sut.searchGuest(guestSearchCondition);

      assertThat(actual)
          .extracting(Guest::getName)
          .containsExactlyInAnyOrder("佐藤花子");
    }

    @Test
    void ユーザー名が一致しない場合_空のリストが返る() {
      GuestSearchCondition guestSearchCondition = new GuestSearchCondition();
      guestSearchCondition.setName("花子");
      guestSearchCondition.setUserId("not-exist");

      List<Guest> actual = sut.searchGuest(guestSearchCondition);

      assertThat(actual).isEmpty();
    }

    @Test
    void 名前と電話番号の複数の条件から一件の宿泊者が取得できる() {
      GuestSearchCondition guestSearchCondition = new GuestSearchCondition();
      guestSearchCondition.setName("佐藤花子");
      guestSearchCondition.setPhone("08098765432");
      guestSearchCondition.setUserId("testuser01");

      List<Guest> actual = sut.searchGuest(guestSearchCondition);

      assertThat(actual)
          .extracting(Guest::getName)
          .containsExactlyInAnyOrder("佐藤花子");
    }

    @Test
    void チェックイン日から一件の宿泊者が取得できる() {
      GuestSearchCondition guestSearchCondition = new GuestSearchCondition();
      guestSearchCondition.setName("佐藤花子");
      guestSearchCondition.setCheckInDate(LocalDate.of(2025, 7, 23));
      guestSearchCondition.setUserId("testuser01");

      List<Guest> actual = sut.searchGuest(guestSearchCondition);

      assertThat(actual)
          .extracting(Guest::getName)
          .containsExactlyInAnyOrder("佐藤花子");
    }

    @Test
    void 該当の宿泊予約日がない場合宿泊者を取得できない() {
      GuestSearchCondition guestSearchCondition = new GuestSearchCondition();
      guestSearchCondition.setName("佐藤花子");
      guestSearchCondition.setCheckInDate(LocalDate.of(2025, 7, 24));
      guestSearchCondition.setUserId("TEST");

      List<Guest> actual = sut.searchGuest(guestSearchCondition);

      assertThat(actual).isEmpty();
    }
  }

  @Nested
  @DisplayName("宿泊者の完全一致検索")
  class matchGuest {

    @Test
    void 名前_かな名_電話番号の組み合わせから宿泊者を取得() {
      GuestMatch guestMatch = new GuestMatch();
      guestMatch.setName("佐藤花子");
      guestMatch.setKanaName("サトウハナコ");
      guestMatch.setPhone("08098765432");
      guestMatch.setUserId("testuser01");

      Guest actual = sut.matchGuest(guestMatch);

      assertThat(actual.getName()).isEqualTo("佐藤花子");
      assertThat(actual.getGender()).isEqualTo("女性");
    }

    @Test
    void 完全一致しない場合_nullが帰ってくる() {
      GuestMatch guestMatch = new GuestMatch();
      guestMatch.setName("佐藤華子");
      guestMatch.setKanaName("サトウハナコ");
      guestMatch.setPhone("08098765432");
      guestMatch.setUserId("testuser01");

      Guest actual = sut.matchGuest(guestMatch);

      assertThat(actual).isNull();
    }

    @Test
    void ユーザー名が一致しない場合_nullが帰ってくる() {
      GuestMatch guestMatch = new GuestMatch();
      guestMatch.setName("佐藤花子");
      guestMatch.setKanaName("サトウハナコ");
      guestMatch.setPhone("08098765432");
      guestMatch.setUserId("not-exist");

      Guest actual = sut.matchGuest(guestMatch);

      assertThat(actual).isNull();
    }
  }

  @Nested
  @DisplayName("宿泊者IDから宿泊者を検索")
  class findByGuestId {

    @Test
    void IDから宿泊者を取得() {
      Guest actual = sut.findGuestById("11111111-1111-1111-1111-111111111111", "testuser01");
      assertThat(actual.getName()).isEqualTo("佐藤花子");
    }

    @Test
    void IDが一致しない場合nullが返ってくる() {
      Guest actual = sut.findGuestById("11111111-1111-1111-1111-111111111112", "testuser01");
      assertThat(actual).isNull();
    }

    @Test
    void ユーザー名が一致しない場合nullが返ってくる() {
      Guest actual = sut.findGuestById("11111111-1111-1111-1111-111111111111", "not-exist");
      assertThat(actual).isNull();
    }
  }

  @Nested
  @DisplayName("宿泊予約IDから宿泊者を検索")
  class findByReservationId {

    @Test
    void IDから宿泊予約が取得できる() {
      Reservation actual = sut.findReservationById("rsv00001-aaaa-bbbb-cccc-000000000001", "testuser01");
      assertThat(actual.getGuestId()).isEqualTo("11111111-1111-1111-1111-111111111111");
    }

    @Test
    void IDが一致しない場合nullが返ってくる() {
      Reservation actual = sut.findReservationById("rsv00001-aaaa-bbbb-cccc-000000000002", "testuser01");
      assertThat(actual).isNull();
    }

    @Test
    void ユーザー名が一致しない場合nullが返ってくる() {
      Reservation actual = sut.findReservationById("rsv00001-aaaa-bbbb-cccc-000000000001",
          "not-exist");
      assertThat(actual).isNull();
    }
  }

  @Nested
  @DisplayName("宿泊プランIDから金額を検索")
  class findTotalPriceById {

    @Test
    void IDから合計金額が取得できる() {
      BigDecimal actual = sut.findTotalPriceById(
          "aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa", "testuser01");
      assertThat(actual).isEqualTo(new BigDecimal("10000.00"));
    }

    @Test
    void IDが一致しない場合_nullが帰ってくる() {
      BigDecimal actual = sut.findTotalPriceById(
          "aaaaaaa0-aaaa-aaaa-aaaa-aaaaaaaaaaaa", "testuser01");
      assertThat(actual).isNull();
    }

    @Test
    void ユーザー名が一致しない場合_nullが帰ってくる() {
      BigDecimal actual = sut.findTotalPriceById(
          "aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa", "not-exist");
      assertThat(actual).isNull();
    }
  }

  @Nested
  @DisplayName("宿泊予約IDから宿泊状態を検索")
  class findStatusById {

    @Test
    void IDから宿泊状態が取得できる() {
      ReservationStatus actual = sut.findStatusById("rsv00001-aaaa-bbbb-cccc-000000000001", "testuser01");
      assertThat(actual).isEqualTo(ReservationStatus.CHECKED_IN);
    }

    @Test
    void IDが一致しない場合_nullが帰ってくる() {
      ReservationStatus actual = sut.findStatusById("rsv00001-aaaa-bbbb-cccc-000000000000", "testuser01");
      assertThat(actual).isNull();
    }

    @Test
    void ユーザー名が一致しない場合_nullが帰ってくる() {
      ReservationStatus actual = sut.findStatusById("rsv00001-aaaa-bbbb-cccc-000000000001",
          "not-exist");
      assertThat(actual).isNull();
    }
  }

  @Nested
  @DisplayName("ユーザーIDからユーザー情報を検索")
  class findUserById {

    @Test
    void IDからユーザー情報が取得できる() {
      User actual = sut.findUserById("testuser01");
      assertThat(actual.getPassword()).isEqualTo("testpass123");
    }

    @Test
    void 一致するIDがない場合_nullが帰ってくる() {
      User actual = sut.findUserById("");
      assertThat(actual).isNull();
    }
  }

  @Nested
  @DisplayName("宿泊者の登録")
  class insertGuest {

    @Test
    void 新規の宿泊者が登録されているか確認() {
      Guest guest = getGuest();
      guest.setId("11111111-1111-1111-1111-111111111115");
      guest.setUserId("testuser01");

      sut.insertGuest(guest);

      List<Guest> actual = sut.findAllGuest("testuser01");
      assertThat(actual)
          .extracting(Guest::getName)
          .containsExactlyInAnyOrder("佐藤花子", "田中太郎","山田毅", "田中武");
    }

    @Test
    void すでに登録されたUUIDを登録しようとした場合_登録に失敗() {
      Guest guest = getGuest();
      guest.setId("11111111-1111-1111-1111-111111111111");
      guest.setUserId("testuser01");

      assertThatThrownBy(() -> sut.insertGuest(guest))
          .isInstanceOf(DuplicateKeyException.class);
    }
  }

  @Nested
  @DisplayName("宿泊プランの登録")
  class insertBooking {

    @Test
    void 宿泊プランが登録されているか確認() {
      Booking booking = getBooking();
      booking.setId("aaaaaa10-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
      booking.setUserId("testuser01");
      sut.insertBooking(booking);

      List<Booking> actual = sut.findAllBooking("testuser01");
      assertThat(actual)
          .extracting(Booking::getName)
          .containsExactlyInAnyOrder("朝食付きプラン", "素泊まりプラン", "夕食付きプラン");
    }

    @Test
    void すでに登録されたUUIDを登録しようとした場合_登録に失敗() {
      Booking booking = getBooking();
      booking.setId("aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
      booking.setUserId("testuser01");

      assertThatThrownBy(() -> sut.insertBooking(booking))
          .isInstanceOf(DuplicateKeyException.class);
    }
  }

  @Nested
  @DisplayName("宿泊予約の登録")
  class insertReservation {

    @Test
    void 宿泊予約が登録されているか確認() {
      Reservation reservation = getReservation();
      reservation.setId("aaaaaaa8-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
      reservation.setUserId("testuser01");
      sut.insertReservation(reservation);

      List<Reservation> actual = sut.findAllReservation(reservation.getUserId());
      assertThat(actual.size()).isEqualTo(4);
    }

    @Test
    void すでに登録されたUUIDを登録しようとした場合_登録に失敗() {
      Reservation reservation = getReservation();
      reservation.setId("rsv00001-aaaa-bbbb-cccc-000000000001");
      reservation.setUserId("testuser01");

      assertThatThrownBy(() -> sut.insertReservation(reservation))
          .isInstanceOf(DuplicateKeyException.class);
    }
  }

  @Nested
  @DisplayName("宿泊者情報の変更")
  class updateGuest {

    @Test
    void 電話番号が更新がされている() {
      Guest before = sut.findGuestById(
          "11111111-1111-1111-1111-111111111111", "testuser01");
      assertThat(before).isNotNull();
      String beforePhone = before.getPhone();

      Guest update = new Guest();
      update.setId("11111111-1111-1111-1111-111111111111");
      update.setName("佐藤花子");
      update.setKanaName("サトウハナコ");
      update.setGender("女性");
      update.setAge(28);
      update.setRegion("東京");
      update.setEmail("hanako@example.com");
      update.setPhone("07098765432");
      sut.updateGuest(update, "testuser01");

      Guest actual = sut.findGuestById("11111111-1111-1111-1111-111111111111", "testuser01");

      assertThat(actual.getPhone()).isEqualTo("07098765432");
      assertThat(actual.getPhone()).isNotEqualTo(beforePhone);
    }

    @Test
    void IDが一致しない場合_電話番号が更新されない() {
      Guest before = sut.findGuestById("11111111-1111-1111-1111-111111111111", "testuser01");
      assertThat(before).isNotNull();
      String beforePhone = before.getPhone();

      Guest update = new Guest();
      update.setId("11111111-1111-1111-1111-111111111111");
      update.setName("佐藤花子");
      update.setKanaName("サトウハナコ");
      update.setGender("女性");
      update.setAge(28);
      update.setRegion("東京");
      update.setEmail("hanako@example.com");
      update.setPhone("07098765432");

      sut.updateGuest(update, "not-exist");

      Guest actual = sut.findGuestById("11111111-1111-1111-1111-111111111111", "testuser01");
      assertThat(actual).isNotNull();
      assertThat(actual.getPhone()).isEqualTo(beforePhone);
    }
  }

  @Nested
  @DisplayName("宿泊予約の変更")
  class updateReservation {

    @Test
    void 備考欄が更新されている() {
      Reservation before = sut.findReservationById("rsv00001-aaaa-bbbb-cccc-000000000001", "testuser01");
      assertThat(before).isNotNull();
      String beforeMemo = before.getMemo();

      before.setMemo("更新");
      sut.updateReservation(before, "testuser01");

      Reservation actual = sut.findReservationById("rsv00001-aaaa-bbbb-cccc-000000000001", "testuser01");

      assertThat(actual).isNotNull();
      assertThat(actual.getMemo()).isEqualTo("更新");
      assertThat(actual.getMemo()).isNotEqualTo(beforeMemo);
    }

    @Test
    void IDが一致しない場合_備考欄が更新されない() {
      Reservation before = sut.findReservationById("rsv00001-aaaa-bbbb-cccc-000000000001", "testuser01");
      assertThat(before).isNotNull();
      String beforeMemo = before.getMemo();

      before.setMemo("更新");
      sut.updateReservation(before, "not-exist");

      Reservation actual = sut.findReservationById("rsv00001-aaaa-bbbb-cccc-000000000001", "testuser01");

      assertThat(actual).isNotNull();
      assertThat(actual.getMemo()).isEqualTo(beforeMemo);
    }
  }


  @Nested
  @DisplayName("宿泊者の論理削除")
  class logicalDeleteGuest {

    @Test
    void 削除フラグがFalseからTrueになっていること() {
      sut.toggleGuestDeletedFlag("11111111-1111-1111-1111-111111111111", "testuser01");
      Guest actual = sut.findGuestById("11111111-1111-1111-1111-111111111111", "testuser01");

      assertThat(actual).isNotNull();
      assertThat(actual.getDeleted()).isTrue();
    }

    @Test
    void 削除フラグがTrueからFalseになっていること() {
      sut.toggleGuestDeletedFlag("22222222-2222-2222-2222-222222222222", "testuser01");
      Guest actual = sut.findGuestById("22222222-2222-2222-2222-222222222222", "testuser01");

      assertThat(actual).isNotNull();
      assertThat(actual.getDeleted()).isFalse();
    }

    @Test
    void IDが一致しない場合更新処理が行われない() {
      sut.toggleGuestDeletedFlag("11111111-1111-1111-1111-111111111112", "testuser01");
      Guest actual = sut.findGuestById("11111111-1111-1111-1111-111111111111", "testuser01");

      assertThat(actual).isNotNull();
      assertThat(actual.getDeleted()).isFalse();
    }

    @Test
    void ユーザー名が一致しない場合_削除フラグが更新されない() {
      sut.toggleGuestDeletedFlag("11111111-1111-1111-1111-111111111111", "not-exist");
      Guest actual = sut.findGuestById("11111111-1111-1111-1111-111111111111", "testuser01");

      assertThat(actual).isNotNull();
      assertThat(actual.getDeleted()).isFalse();
    }
  }



  @Nested
  @DisplayName("チェックイン処理")
  class checkIn {

    @Test
    void ステータスがチェックインに変更されている() {
      sut.checkIn("rsv00002-bbbb-cccc-dddd-000000000002", "testuser01");
      Reservation actual = sut.findReservationById("rsv00002-bbbb-cccc-dddd-000000000002", "testuser01");

      assertThat(actual.getStatus()).isEqualTo(ReservationStatus.CHECKED_IN);
    }

    @Test
    void ユーザーIDが一致しない場合_ステータスが変更されない() {
      sut.checkIn("rsv00002-bbbb-cccc-dddd-000000000002", "not-exist");
      Reservation actual = sut.findReservationById("rsv00002-bbbb-cccc-dddd-000000000002", "testuser01");

      assertThat(actual.getStatus()).isNotEqualTo(ReservationStatus.CHECKED_IN);
    }
  }

  @Nested
  @DisplayName("チェックアウト処理")
  class checkOut {

    @Test
    void ステータスがチェックアウトに変更されている() {
      sut.checkOut("rsv00001-aaaa-bbbb-cccc-000000000001", "testuser01");
      Reservation actual = sut.findReservationById("rsv00001-aaaa-bbbb-cccc-000000000001", "testuser01");

      assertThat(actual.getStatus()).isEqualTo(ReservationStatus.CHECKED_OUT);
    }

    @Test
    void ユーザーIDが一致しない場合_ステータスが変更されない() {
      sut.checkOut("rsv00001-aaaa-bbbb-cccc-000000000001", "not-exist");
      Reservation actual = sut.findReservationById("rsv00001-aaaa-bbbb-cccc-000000000001", "testuser01");

      assertThat(actual.getStatus()).isNotEqualTo(ReservationStatus.CHECKED_OUT);
    }
  }

  @Nested
  @DisplayName("ユーザーの登録処理")
  class insertUser {

    @Test
    void ユーザーが登録されていること() {
      User actual = new User();
      actual.setId("123");
      actual.setPassword("123");

      sut.insertUser(actual);
      User update = sut.findUserById("123");

      assertThat(actual.getId()).isEqualTo(update.getId());
    }
  }

  // 生成用
  private Guest getGuest() {
    Guest guest = new Guest();
    guest.setName("田中武");
    guest.setKanaName("タナカタケシ");
    guest.setGender("男性");
    guest.setRegion("山形県");
    guest.setAge(28);
    guest.setEmail("takeshi@example.com");
    guest.setPhone("08098765432");
    return guest;
  }

  private Booking getBooking() {
    Booking booking = new Booking();
    booking.setName("夕食付きプラン");
    booking.setPrice(BigDecimal.valueOf(1000));
    booking.setDescription("");

    return booking;
  }

  private Reservation getReservation() {
    Reservation reservation = new Reservation();
    reservation.setGuestId("11111111-1111-1111-1111-111111111111");
    reservation.setBookingId("aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    reservation.setTotalPrice(BigDecimal.valueOf(2000));
    reservation.setCheckInDate(LocalDate.now());
    reservation.setStayDays(1);
    reservation.setCheckOutDate(LocalDate.now().plusDays(reservation.getStayDays()));
    reservation.setStatus(ReservationStatus.NOT_CHECKED_IN);
    reservation.setMemo("");
    return reservation;
  }

  private static String getUserId() {
    return "TEST";
  }
}