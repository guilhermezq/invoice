package eu.finbite.invoice.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InvoiceEntry {

  private String description;
  private Integer value;
}
