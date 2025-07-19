package com.portfolio.hotel.management.data.guest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Guest {

  @Pattern(
      regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
      message = "IDはUUID形式である必要があります"
  )
  private String id;

  @NotBlank(message = "名前は必須です")
  private String name;

  @NotBlank(message = "ふりがなは必須です")
  private String kanaName;

  @NotBlank(message = "性別は必須です")
  private String gender;

  @NotNull(message = "年齢は必須です")
  @PositiveOrZero(message = "年齢は0以上である必要があります")
  private Integer age;

  @NotBlank(message = "地域は必須です")
  private String region;

  @NotBlank(message = "メールアドレスは必須です")
  @Email(message = "正しいメールアドレスを入力してください")
  private String email;

  @NotBlank(message = "電話番号は必須です")
  @Pattern(regexp = "^[0-9]{10,11}$", message = "電話番号は10〜11桁の数字で入力してください")
  private String phone;

  @NotNull(message = "削除フラグは必須です")
  private Boolean deleted = false;
}