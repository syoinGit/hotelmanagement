package com.portfolio.hotel.management.data.guest;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

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
