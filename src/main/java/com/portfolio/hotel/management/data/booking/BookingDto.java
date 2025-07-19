package com.portfolio.hotel.management.data.booking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.UUID;
import jdk.jfr.BooleanFlag;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class BookingDto {

  @Pattern(
      regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
      message = "IDはUUID形式である必要があります"
  )
  private String id;

  @NotBlank(message = "名前は必須です")
  private String name;

  private String description;

  @NotNull(message = "値段は必須です")
  private BigDecimal price;
}