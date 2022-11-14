package eu.finbite.invoice.services.calculator;

import eu.finbite.invoice.models.Package;
import eu.finbite.invoice.models.PriceList;
import eu.finbite.invoice.models.Usage;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(2)
@Component
public class SmsCalculator extends Calculator {
  @Override
  protected Integer calculate(Usage usage, PriceList priceList, Package pack) {
    return Math.max(0, usage.getSms() - pack.getSms()) * priceList.getSmsPriceInCents();
  }

  @Override
  protected String getDescription(Usage usage, Package pack) {
    return String.format("SMS - %d", Math.max(0, usage.getSms() - pack.getSms()));
  }




}
