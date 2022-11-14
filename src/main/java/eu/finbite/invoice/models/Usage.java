package eu.finbite.invoice.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Usage {

  private Integer minutes;
  private Integer sms;
}
