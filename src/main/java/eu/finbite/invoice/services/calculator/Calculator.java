package eu.finbite.invoice.services.calculator;

import eu.finbite.invoice.models.InvoiceEntry;
import eu.finbite.invoice.models.Package;
import eu.finbite.invoice.models.PriceList;
import eu.finbite.invoice.models.Usage;

public abstract class Calculator {

  abstract Integer calculate(Usage usage, PriceList priceList, Package pack);

  abstract String getDescription(Usage usage, Package pack);

  public InvoiceEntry getEntry(Usage usage, PriceList priceList, Package pack) {
    return InvoiceEntry.builder().description(getDescription(usage,pack)).value(calculate(usage, priceList, pack)).build();
  }
}
