package com.portfolio.hotel.management.data.guest;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestSearchCondition {

 private String name;

 private String kanaName;

 private String phone;

 private LocalDate checkInDate;

 private LocalDate checkOutDate;

 private String userId;
}