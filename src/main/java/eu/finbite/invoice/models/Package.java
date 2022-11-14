package eu.finbite.invoice.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Package {
  SMALL("S", 10, 50, 50_0),
  MEDIUM("M", 50, 100, 10_00),
  LARGE("L", 500, 500, 20_00);

  private final String packageName;
  private final Integer minutes;
  private final Integer sms;
  private final Integer priceInCents;
}
