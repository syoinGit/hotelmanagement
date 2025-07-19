package com.portfolio.hotel.management.data.reservation;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ReservationDto {

  @NotBlank(message = "IDは必須です")
  @Pattern(
      regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
      message = "IDはUUID形式である必要があります"
  )
  private String id;

  @NotBlank(message = "IDは必須です")
  @Pattern(
      regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
      message = "IDはUUID形式である必要があります"
  )
  private String guestId;

  @NotBlank(message = "IDは必須です")
  @Pattern(
      regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
      message = "IDはUUID形式である必要があります"
  )
  private String bookingId;

  @NotBlank
  @FutureOrPresent(message = "チェックイン日に過去の日付は使用できません")
  private LocalDate checkInDate;

  @NotBlank(message = "滞在日は必須です")
  private Integer stayDays;

  @NotNull(message = "総額は必須です")
  private BigDecimal totalPrice;


  @NotBlank(message = "滞在状況は必須です")
  private ReservationStatus status;

  private String memo;

  @NotBlank
  private LocalDateTime createdAt = LocalDateTime.now();
}