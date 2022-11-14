package eu.finbite.invoice.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Invoice {

  private List<InvoiceEntry> entries = new ArrayList<>();
  private Integer total = 0;

  public void addEntry(InvoiceEntry entry) {
    entries.add(entry);
  }
}
