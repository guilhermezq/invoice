package eu.finbite.invoice.services.calculator;

import eu.finbite.invoice.models.Package;
import eu.finbite.invoice.models.PriceList;
import eu.finbite.invoice.models.Usage;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(3)
@Component
public class PackageCalculator extends Calculator {
  @Override
  protected Integer calculate(Usage usage, PriceList priceList, Package pack) {
    return pack.getPriceInCents();
  }

  @Override
  protected String getDescription(Usage usage, Package pack) {
    return String.format("Plan %s", pack.getPackageName());
  }
}
