package com.portfolio.hotel.management.data.guest;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestSearchCondition {

  String name;

  String kanaName;

  String phone;

  LocalDate checkInDate;

  LocalDate checkOutDate;
}