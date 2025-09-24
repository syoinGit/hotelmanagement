# 🏨 Hotel Management App

<img width="800" alt="top" src="https://github.com/user-attachments/assets/a1194a7a-65f9-4ccd-91e8-9ff5242f96c9" />

小規模宿泊施設向けの **宿泊者・予約管理アプリ**  
Spring Boot (バックエンド) + React (フロント) 構成で、タブレット1台で完結する運用を想定しました。

### アプリケーションURL
http://hotelmanagement-env.eba-jf25s8xt.ap-northeast-1.elasticbeanstalk.com

## ✨ 本アプリの特徴
- 宿泊者情報の管理と登録、チェックインとアウトの宿泊業務
- 完全一致検索による宿泊者の重複登録防止
- Spring Securityによるログイン・セッション管理

---

## 📌 制作背景
一人旅でゲストハウスを利用した際、紙で宿泊予約の管理をしている場面に遭遇。<br>
検索性の向上やチェックイン状況の曖昧さを改善するために作成しました。

---
## 🛠使用技術

### バックエンド
- Java 21 / Spring Boot
- Spring Security
- MyBatis / MySQL(デプロイ環境ではh2を使用)
- JUnit

### フロントエンド
- React 18
- Axios（API通信）
- TailwindCSS

### インフラ
- AWS Elastic Beanstalk

## 📋 機能一覧

| 機能カテゴリ   | 機能内容 |
|----------------|--------------------------------------|
| ログイン機能   | ユーザーログイン（Spring Security） |
| 登録処理 | 宿泊者登録 / 宿泊予約の登録 |
| 宿泊者管理     |  検索 / 更新 / 論理削除（復元可） |
| 業務処理       | チェックイン / チェックアウト |
| テスト      | Repository・Service・Controller 単体テスト|
---

## 🔑 ログイン機能
<img width="800" alt="login" src="https://github.com/user-attachments/assets/942a74f4-a535-4d4d-9e54-a4f97bf0fdb7" />
<br><br>
Spring Security による認証を実装しています。<br>  
ログイン成功時にCookieが発行され、<br>
以降のAPIでは`Authentication`を通してユーザーIDを取得できます。

```java
// SecurityConfig 抜粋
.formLogin(form -> form
    .loginProcessingUrl("/login")
    .usernameParameter("id")
    .passwordParameter("password")
    .successHandler((req, res, auth) -> {
        res.setStatus(HttpServletResponse.SC_OK);
        res.setContentType("application/json");
        res.getWriter().write("{\"message\": \"Login successful\"}");
    })
)
.userDetailsService(service);

// HotelService - Spring Security による認証処理
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
```
---
## 🏠　ホーム画面
<img width="800" alt="Home" src="https://github.com/user-attachments/assets/41f9a02f-cdf5-472b-8b44-f297a07718a3" />
<br><br>
現在宿泊中の宿泊者を一覧表示します。<br>
バックエンドでは ログインユーザーの ID を Authentication から抽出し、<br>
そのユーザーに紐づく宿泊者を SQL で取得しています。

```java

// Controller
  @GetMapping("/guests/stay")
  public List<GuestDetail> getStay(Authentication authentication) {
    return service.getStayNow(authentication);
  }

// Service -- 現在宿泊中の宿泊者情報を作成
  public List<GuestDetail> getStayNow(Authentication authentication) {
    String userId = extractLoginId(authentication);　// ← ユーザーIDの取得
    return converter.convertGuestDetail(
        repository.findGuestStayNow(userId),　// ← ユーザーIDが一致するものを取得
        repository.findAllBooking(userId),
        repository.findReservationStayNow(userId));
  }


```

---
## 📝 予約の登録
<img width="800" alt="match" src="https://github.com/user-attachments/assets/05f92de7-8c08-4c8b-8a21-27970afd4188" />
<br><br>
名前 / フリガナ / 電話番号で完全一致検索を行います。<br>
既存の情報があれば追加登録、なければ新規登録を行います。

```java

👤 完全一致検索

// Controller　　
@PostMapping("/guest/match")
public GuestRegistration matchGuestForInsert(
        Authentication authentication,
        @RequestBody @Valid GuestMatch guestMatch) {
    return service.matchGuest(authentication, guestMatch);
}

// Service -- 完全一致検索
public GuestRegistration matchGuest(Authentication authentication, GuestMatch guestMatch) {
    guestMatch.setUserId(authentication.getName());
    Guest guest = repository.matchGuest(guestMatch);

```
---

### 
<img width="800" alt="match" src="https://github.com/user-attachments/assets/5c0e87f4-8213-418f-bf49-84e08131b035" />


```java

    GuestRegistration guestRegistration = new GuestRegistration();
    if (guest != null) {
        guestRegistration.setGuest(guest);

```

---
## 👤 宿泊者/予約の登録

<img width="800" alt="register" src="https://github.com/user-attachments/assets/0a10625a-ea5d-44ce-a6a7-db245056cfa5" />

<br><br>
登録ボタンを押すとモーダルが開き、登録処理が行われます。<br>

```java

// Controller
@PutMapping("/guest/register")
public ResponseEntity<String> registerGuest(
        Authentication authentication,
        @RequestBody @Valid GuestRegistration guestRegistration) {
    service.registerGuest(authentication, guestRegistration);
    return ResponseEntity.ok("宿泊者情報の登録が完了しました。");
}

// Service -- 宿泊者の登録
  public void registerGuest(Authentication authentication, GuestRegistration guestRegistration) {
    guestRegistration.getGuest().setUserId(extractLoginId(authentication));
   
    if (guestRegistration.getGuest().getId() == null) { // <- IDが登録されているかどうかで分岐
      guestRegistration.getGuest().setId(UUID.randomUUID().toString());
      repository.insertGuest(guestRegistration.getGuest());
    }
    initReservation(guestRegistration);
  }

  // 宿泊予約の登録
  private void initReservation(GuestRegistration guestRegistration) {
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

```
___
## 👤 宿泊者一覧

<img width="800" alt="guests" src="https://github.com/user-attachments/assets/0bc0c7dc-4308-4439-ba5e-5d15212720ed" />

宿泊者情報を確認できます。<br>
上部の検索欄に情報を入力すると、詳細検索ができます。

```java

// Controller
  @PostMapping("/guest/search")
  public List<GuestDetail> searchGuest(Authentication authentication,
      @RequestBody GuestSearchCondition guestSearchCondition) {
    return service.searchGuest(authentication, guestSearchCondition);
  }

// service
  public List<GuestDetail> searchGuest(
      Authentication authentication,
      GuestSearchCondition guestSearchCondition) {
    String userId = extractLoginId(authentication);
    guestSearchCondition.setUserId(userId);

    return converter.convertGuestDetail(
        repository.searchGuest(guestSearchCondition),
        repository.findAllBooking(userId),
        repository.findAllReservation(userId));
  }

```

## 情報の更新
<img width="800" alt="update" src="https://github.com/user-attachments/assets/79516fb8-3f92-4e8c-b9b0-bb7123040323" />

<br><br>
情報を編集ボタンを押すと編集画面のモーダルが開き、入力された内容を更新します。<br>
宿泊者、宿泊予約の両方が更新可能です。
<br>

加えて、削除フラグの切り替えも可能です。

```java
 // Controller
  @PutMapping("/guest/update")
  public ResponseEntity<String> updateGuest(Authentication authentication,
      @RequestBody Guest guest) {
    service.updateGuest(authentication, guest);
    return ResponseEntity.ok("宿泊者の更新が完了しました。");
  }

  // Service
  public void updateGuest(Authentication authentication, Guest guest) {
    repository.updateGuest(guest, extractLoginId(authentication));
  }

@PutMapping("/guest/deleted")
  public ResponseEntity<String> logicalDeleteGuest(
      @RequestParam String id, @RequestParam String name,
      Authentication authentication) {
    service.logicalDeleteGuest(authentication, id);
    return ResponseEntity.ok(name + "様の情報を削除しました。");
  }

 public void logicalDeleteGuest(Authentication authentication, String id) {
    repository.toggleGuestDeletedFlag(id, extractLoginId(authentication));
  }
  
```
<img width="800" alt="cack" src="https://github.com/user-attachments/assets/53a49818-7790-434b-941f-8498d4f56e0f" />

---
## 🏠チェックイン・チェックアウト

<br><br>
ページを開くと本日チェックイン予定の宿泊者が表示されます。

```java
// Controller -- ユーザーIDと今日の日付をServiceに渡す
@GetMapping("/guests/check-in-today")
  public List<GuestDetail> getChackInToday(Authentication authentication) {
    LocalDate today = LocalDate.now();
    return service.getCheckInToday(authentication, today);
  }

// Sercice
  public List<GuestDetail> getCheckInToday(Authentication authentication, LocalDate today) {
    String userId = extractLoginId(authentication);
    return converter.convertGuestDetail(
        repository.findGuestsTodayCheckIn(userId, today),
        repository.findAllBooking(userId),
        repository.findReservationTodayCheckIn(userId, today));
  }
```
<br><br>
<img width="800" alt="co" src="https://github.com/user-attachments/assets/d7c042ec-f3db-4293-baad-aae5a027c544" />
<br><br>
チェックインボタンを押すと、チェックインの処理が実行されます。
<br>チェックアウト処理も、同様の処理を行います。

```java

// Controrrer
  @PutMapping("/guest/checkIn")
  public ResponseEntity<String> checkIn(
      Authentication authentication,
      @RequestParam String id,
      @RequestParam String name) {
    service.checkIn(authentication, id);
    return ResponseEntity.ok(name + "様のチェックインが完了しました。");
  }

// Service
public void checkIn(Authentication authentication, String id) {
    ReservationStatus status = repository.findStatusById(id, extractLoginId(authentication)); // <- 現在のチェックイン状態を取得
    if (status == ReservationStatus.NOT_CHECKED_IN) { // <- 未チェックインの場合チェックイン処理を行う
      repository.checkIn(id, extractLoginId(authentication));
    } else {
      throw new IllegalStateException("未チェックインの予約のみチェックイン可能です");
    }
  }
```

---
## 🛠️自動テスト
### Repository：SQLクエリの動作確認（存在/非存在パターン）
```java

// repository抜粋
  @Nested
  @DisplayName("宿泊者の全件検索")
  class findAllGuest {

    @Test
    void 登録された2件の宿泊者が取得できる() {
      List<Guest> actual = sut.findAllGuest(getUserId());

      assertThat(actual)
          .extracting(Guest::getName)
          .containsExactlyInAnyOrder("佐藤花子", "田中太郎");
    }

    @Test
    void ユーザーIDが一致しなかった場合_空のリストが返る() {
      List<Guest> actual = sut.findAllGuest("not-exist");
      assertThat(actual).isEmpty();
    }
```

### Service：Repository・Converter呼び出し検証（Mockito）
```java

// Service抜粋
  @Test
  void 宿泊者情報の全件検索_リポジトリとコンバーターが呼び出せている() {
    HotelService sut = new HotelService(repository, converter);
    Authentication auth = getAuthentication();
    String userId = getUserId(auth);

    List<Guest> guest = new ArrayList<>();
    List<Booking> booking = new ArrayList<>();
    List<Reservation> reservation = new ArrayList<>();
    List<GuestDetail> converted = new ArrayList<>();

    when(repository.findAllGuest(userId)).thenReturn(guest);
    when(repository.findAllBooking(userId)).thenReturn(booking);
    when(repository.findAllReservation(userId)).thenReturn(reservation);
    when(converter.convertGuestDetail(guest, booking, reservation))
        .thenReturn(converted);

    List<GuestDetail> actual = sut.getAllGuest(auth);

    verify(repository, times(1)).findAllGuest(userId);
    verify(repository, times(1)).findAllBooking(userId);
    verify(repository, times(1)).findAllReservation(userId);

    verify(converter, times(1))
        .convertGuestDetail(guest, booking, reservation);

    assertNotNull(actual);
    assertEquals(actual, converted);
  }
```

### Controller：MockMvcでエンドポイント疎通テスト

```java

  @Test
  @WithMockUser(username = "TEST", roles = "USER")
  void 宿泊者情報の全件検索_空のリストが帰ってくること() throws Exception {
    mockMvc.perform(get("/guests"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));
    verify(service, times(1)).getAllGuest(any(Authentication.class));
  }
```
___
## 🎯こだわった点

実際にホテルフロント業務に携わった際、宿泊者と宿泊予約が1:1で紐づいている運用に課題を感じました。<br>
この仕組みの影響で、同姓同名の宿泊者が存在すると、検索画面に「同じ名前が複数表示され、<br>
どの予約がどの宿泊者か分からない」という状態が発生していました。

この問題を解決するためにこのアプリでは、
<br>登録時に完全一致検索を挟むことで一人の宿泊者に対して複数の予約データを無理なく紐付け、
- 情報入力工程の削減
- 検索時の利便性の向上
- 常連の宿泊者の把握
<br>
が可能になっています。

___
## 🚀 今後の展望
- 決済機能の実装（クレジット・QR対応）
- ルーム管理機能（部屋ごとの稼働状況の可視化）
