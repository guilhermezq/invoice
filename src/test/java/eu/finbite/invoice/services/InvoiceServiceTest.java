package eu.finbite.invoice.services;

import eu.finbite.invoice.models.Invoice;
import eu.finbite.invoice.models.Package;
import eu.finbite.invoice.models.PriceList;
import eu.finbite.invoice.models.Usage;
import eu.finbite.invoice.services.calculator.Calculator;
import eu.finbite.invoice.services.calculator.MinutesCalculator;
import eu.finbite.invoice.services.calculator.PackageCalculator;
import eu.finbite.invoice.services.calculator.SmsCalculator;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class InvoiceServiceTest {

  private InvoiceService invoiceService;

  @BeforeEach
  public void init()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    List<Calculator> calculators = new ArrayList<>();
    calculators.add(new MinutesCalculator());
    calculators.add(new SmsCalculator());
    calculators.add(new PackageCalculator());
    invoiceService = new InvoiceService(calculators);
    Method postConstructMethod = InvoiceService.class.getDeclaredMethod("init");
    postConstructMethod.setAccessible(true);
    postConstructMethod.invoke(invoiceService);
  }

  @ParameterizedTest
  @CsvFileSource(resources = "/calculateInvoiceTotalInputs.csv", delimiter = ';')
  public void calculateInvoiceTotalTest(
      Integer usageMinutes,
      Integer usageSms,
      Integer priceListMinutesPriceInCents,
      Integer priceListSmsPriceInCents,
      Package pack,
      Integer outputMinutesPriceInCents,
      Integer outputSmsPriceInCents,
      Integer outputTotal) {
    Usage usage = Usage.builder().sms(usageSms).minutes(usageMinutes).build();
    PriceList priceList =
        PriceList.builder()
            .minutesPriceInCents(priceListMinutesPriceInCents)
            .smsPriceInCents(priceListSmsPriceInCents)
            .build();
    Invoice invoice = invoiceService.calculateInvoiceTotal(usage, priceList, pack);
    SoftAssertions assertBundle = new SoftAssertions();
    assertBundle.assertThat(invoice).hasFieldOrPropertyWithValue("total", outputTotal);
    assertBundle
        .assertThat(invoice.getEntries())
        .filteredOn(entry -> entry.getDescription().contains("SMS - "))
        .first()
        .hasFieldOrPropertyWithValue("value", outputSmsPriceInCents);
    assertBundle
            .assertThat(invoice.getEntries())
            .filteredOn(entry -> entry.getDescription().contains("Minutes - "))
            .first()
            .hasFieldOrPropertyWithValue("value", outputMinutesPriceInCents);
    assertBundle.assertAll();
  }
}
