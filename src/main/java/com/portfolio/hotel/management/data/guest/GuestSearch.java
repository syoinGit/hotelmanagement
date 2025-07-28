package com.portfolio.hotel.management.data.guest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestSearch {

  @NotBlank(message = "名前は必須です")
  private String name;

  @NotBlank(message = "ふりがなは必須です")
  private String kanaName;

  @NotBlank(message = "電話番号は必須です")
  @Pattern(regexp = "^[0-9]{10,11}$", message = "電話番号は10〜11桁の数字で入力してください")
  private String phone;

}
