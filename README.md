<img width="800" height="800" alt="mainmenu" src="https://github.com/user-attachments/assets/bdd93be6-bacc-41b8-859c-bc81af37cf77" />

---

# 📌 制作背景

一人旅の中で、ゲストハウスや民宿を利用する機会が多くありました。  
そういった小規模な宿泊施設では、宿泊者情報やチェックイン状況を紙で管理しているケースが多く、<br>
情報の整理や管理が煩雑になりがちです。

本アプリは、**「iPad1台で完結するような宿泊者管理ツール」**を目指し、  
チェックイン業務や宿泊者情報の管理を効率化できるように設計しました。  
特に、IT環境が整っていない施設でも直感的に使えるよう、シンプルかつ視認性の高いUI/UXを意識しています。

---

# 🛠 使用技術

- Java 21  
- Spring Boot (Framework)  
- MySQL  
- React  
- JavaScript  

---

# 📋 機能一覧
| 機能カテゴリ       | 機能内容 |
|--------------------|--------------------------------------------|
| 宿泊者管理| ・宿泊者情報の一覧表示<br>・宿泊者の検索<br>・論理削 |
| 予約管理 | ・新規予約の登録<br>・チェックイン / チェックアウト|
| 宿泊プラン管理| ・宿泊プランの一覧表示<br>・宿泊プランの登録 |

# 新規予約の登録

<img width="1000" height="1000" alt="スクリーンショット03" src="https://github.com/user-attachments/assets/28149829-28a7-451a-b2b8-4136afb259c9" />
「新規予約登録」ボタンをタップすると、名前・ふりがな・電話番号を入力して宿泊者を検索する画面が表示されます。
<br><br>
検索結果が既存の宿泊者情報と完全一致する場合、その情報が自動的に入力されます。<br>
一致しない場合は、入力された情報のみがフォームに自動入力されます。

<pre><code>
HotelController
  
  @Operation(summary = "完全一致検索", description = "名前、ふりがな、電話番号から宿泊者情報を完全一致検索します。
  ここで完全位一致したデータは宿泊者情報登録の際に使われます")
  @PostMapping("/matchGuest")
  public GuestDetail matchGuestForInsert(@RequestBody @Valid GuestMatch guestMatch) {
    return service.matchGuest(guestMatch);
  }
  </code></pre>

<img width="800" height="800" alt="スクリーンショット04" src="https://github.com/user-attachments/assets/ac4b0d95-1827-4fdb-856b-33160bd4e71e" />


一致する宿泊者が存在しない場合、入力された情報を基にGuestDetailを作成して返却します。

<pre><code>
  HotelServie
  
  // 宿泊者の完全一致検索
  public GuestDetail matchGuest(GuestMatch guestMatch) {
    Guest guest = repository.matchGuest(guestMatch);
    GuestDetail guestDetail = new GuestDetail();
    // 一致するものがなかった場合、guestの数値を入れる。
    if (guest == null) {
      guestDetail.setGuest(converter.toGuest(guestMatch));
  </code></pre>


<img width="800" height="800" alt="スクリーンショット05" src="https://github.com/user-attachments/assets/6ce84d67-8eb7-41f1-b544-6d99a813263b" />

一致する場合は、既存の宿泊者情報をGuestDetailとして返します。
（GuestRegistrationで返すように今後変更予定）

<pre><code>
  HotelServie
  
  // 宿泊者の完全一致検索
  public GuestDetail matchGuest(GuestMatch guestMatch) {
    Guest guest = repository.matchGuest(guestMatch);
    GuestDetail guestDetail = new GuestDetail();
    // 一致するものがなかった場合、guestの数値を入れる。
    if (guest == null) {
      guestDetail.setGuest(converter.toGuest(guestMatch));
      // 一致した場合、取得したguestを入れる。
    } else {
      guestDetail.setGuest(guest);
    }
    return guestDetail;
  }

</code></pre>

## **宿泊者の登録処理**

<img width="1440" height="900" alt="スクリーンショット07" src="https://github.com/user-attachments/assets/b16262c1-db2e-4700-966f-2a7f6fe38a2c" />

登録処理にはGuestRegistrationクラスを使用します。<br><br>

<pre><code>
public class GuestRegistration {

  private Guest guest;

  @Pattern(
      regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
      message = "IDはUUID形式である必要があります"
  )
  private String bookingId;

  @NotNull(message = "滞在日は必須です")
  private Integer stayDays;

  @NotNull
  @FutureOrPresent(message = "チェックイン日に過去の日付は使用できません")
  private LocalDate checkInDate;

  private String memo;

}
  </code></pre>

GuestDetailを使用しない理由は以下の通りです：<br>
	•	booking情報はIDのみ必要で、<br>
	•	reservationデータはサービス層で作成されるため、<br>
送信されるデータを明確にする目的で個別クラスを使用しています。



コントローラ側では、フロントエンドから送られてくるJSONを受け取って登録処理を行います。<br>
入力がバリデーションに違反していた場合はエラーが返されます。

<pre><code>
HoteLController
  
  @Operation(summary = "宿泊者情報登録", description = "宿泊者情報を入力し、宿泊者情報を登録します。")
  @PutMapping("/registerGuest")
  public ResponseEntity<String> registerGuest(
      @RequestBody @Valid GuestRegistration guestRegistration) {
    service.registerGuest(guestRegistration);
    return ResponseEntity.ok("宿泊者情報の登録が完了しました。");
  }
  
  </code></pre>


## **宿泊者の登録ロジック**

受け取ったGuestRegistrationの内容を元に、宿泊者情報を登録します。<br>

直前の一致検索で既に登録されている宿泊者と一致していた場合、登録処理はスキップされます。<br>
スキップの判断には、IDがnullであるかどうかを条件としています。

<pre><code>
  HotelService
  
  // 宿泊者の登録
  public void registerGuest(GuestRegistration guestRegistration) {
    // 直前の検索で一致する宿泊者がなかった場合新規登録
    if (guestRegistration.getGuest().getId() == null) {
      guestRegistration.getGuest().setId(UUID.randomUUID().toString());
      repository.insertGuest(guestRegistration.getGuest());
    }
    initReservation(guestRegistration);
  }
  </code></pre>

## **宿泊予約の登録**<br>
宿泊者情報を、引数のguestRegistrationを使用して作成します。<br>

チェックイン状態は初期値として「未チェックイン（NOT_CHECKED_IN）」に設定されます。<br>
これは当日のチェックイン処理や検索条件に使用され、<br>
登録直後から「チェックイン予定」ページに表示されるようになります。

<pre><code>
  HotelService
  
  private void initReservation(GuestRegistration guestRegistration) {
    Reservation reservation = new Reservation();

    reservation.setId(UUID.randomUUID().toString());
    reservation.setGuestId(guestRegistration.getGuest().getId());
    reservation.setBookingId(guestRegistration.getBookingId());
    reservation.setCheckInDate(guestRegistration.getCheckInDate());
    reservation.setStayDays(guestRegistration.getStayDays());
    reservation.setCheckOutDate(reservation.getCheckInDate().plusDays(guestRegistration.getStayDays()));
    BigDecimal price = repository.findTotalPriceById(reservation.getBookingId());
    BigDecimal total = price.multiply(BigDecimal.valueOf(reservation.getStayDays()));
    reservation.setTotalPrice(total);
    reservation.setMemo(guestRegistration.getMemo());
    reservation.setStatus(ReservationStatus.NOT_CHECKED_IN);
    reservation.setCheckInDate(LocalDate.now());

    repository.insertReservation(reservation);
  }
  </code></pre>

<img width="1440" height="900" alt="スクリーンショット08" src="https://github.com/user-attachments/assets/89d7a206-3166-4b19-becc-568e7a248e7e" />

登録が完了すると、以下のようなデータ構造で保存されます：

  <pre><code>
    {
        "guest": {
            "id": "4238c959-6f70-40e4-9256-f7bf8b04f473",
            "name": "織田信長",
            "kanaName": "オダノブナガ",
            "gender": "男性",
            "age": 28,
            "region": "滋賀県",
            "email": "nobunaga@ne.jp",
            "phone": "123456789",
            "deleted": false
        },
        "bookings": [
            {
                "id": "044d45d0-c10a-4eb6-9a05-444740f9d9d4",
                "name": "朝食付きプラン",
                "description": "朝食が付いている快適なプランです。",
                "price": 8500.00,
                "isAvailable": true
            }
        ],
        "reservations": [
            {
                "id": "da995b97-5695-4607-a106-8be30a91d98c",
                "guestId": "4238c959-6f70-40e4-9256-f7bf8b04f473",
                "bookingId": "044d45d0-c10a-4eb6-9a05-444740f9d9d4",
                "checkInDate": "2025-07-29",
                "checkOutDate": "2025-08-04",
                "stayDays": 5,
                "totalPrice": 42500.00,
                "status": "NOT_CHECKED_IN",
                "memo": "",
                "createdAt": "2025-07-29T19:33:49"
            }
        ]
  </code></pre>
## **補足:完全一致検索の意図**<br>

実際のホテル現場でフロントシステムを運用していた際、<br>
「同一の宿泊者が名前で複数表示される」ケースが頻発していました。<br>

この問題を回避するため、完全一致による宿泊者検索を導入し、<br>
宿泊者情報と予約情報を自然に紐付けられる仕様としました。<br>

この仕組みによって：<br>
	•	宿泊者情報の入力ミス防止や<br>
	•	入力作業の時短<br>
が実現でき、特に小規模宿泊施設においては、<br>
	•	常連客や継続利用者の把握がしやすくなり、<br>
	•	顔なじみの対応や個別サービスが可能になります。<br>
<br>
結果として、小さな宿の強みを活かす運用が可能となります。
