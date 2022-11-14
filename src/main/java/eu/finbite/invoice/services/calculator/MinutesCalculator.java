package eu.finbite.invoice.services.calculator;

import eu.finbite.invoice.models.Package;
import eu.finbite.invoice.models.PriceList;
import eu.finbite.invoice.models.Usage;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Component
public class MinutesCalculator extends Calculator {

  @Override
  protected Integer calculate(Usage usage, PriceList priceList, Package pack) {
    return Math.max(0, usage.getMinutes() - pack.getMinutes()) * priceList.getMinutesPriceInCents();
  }

  @Override
  protected String getDescription(Usage usage, Package pack) {
    return String.format("Minutes - %d", Math.max(0, usage.getMinutes() - pack.getMinutes()));
  }
}
