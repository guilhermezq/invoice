package eu.finbite.invoice.services;

import eu.finbite.invoice.dtos.UsageDto;
import eu.finbite.invoice.models.Invoice;
import eu.finbite.invoice.models.InvoiceEntry;
import eu.finbite.invoice.models.Package;
import eu.finbite.invoice.models.PriceList;
import eu.finbite.invoice.models.Usage;
import eu.finbite.invoice.services.calculator.Calculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Log4j2
@Service
@RequiredArgsConstructor
public class InvoiceService {

  public static int SMS_PRICE_IN_CENTS = 30;
  public static int MINUTES_PRICE_IN_CENTS = 20;

  private final List<Calculator> calculators;

  private PriceList priceList;
  private NumberFormat currencyFormatter;

  @PostConstruct
  private void init() {
    priceList =
        PriceList.builder()
            .smsPriceInCents(SMS_PRICE_IN_CENTS)
            .minutesPriceInCents(MINUTES_PRICE_IN_CENTS)
            .build();
    Locale locale = new Locale("en", "EE");
    currencyFormatter = NumberFormat.getCurrencyInstance(locale);
  }

  private String formatCurrency(Integer value) {
    return currencyFormatter.format(Double.valueOf(value) / 100);
  }

  public Invoice calculateInvoiceTotal(Usage usage, PriceList priceList, Package pack) {
    final Invoice invoice = new Invoice();
    calculators
        .forEach(
            calculator -> {
              InvoiceEntry invoiceEntry = calculator.getEntry(usage, priceList, pack);
              log.info(
                  "{} - {}",
                  invoiceEntry.getDescription(),
                  formatCurrency(invoiceEntry.getValue()));
              invoice.addEntry(invoiceEntry);
              invoice.setTotal(invoice.getTotal() + invoiceEntry.getValue());
            });
    log.info("TOTAL - {}", formatCurrency(invoice.getTotal()));
    return invoice;
  }

  public byte[] generatePdfDocument(UsageDto usageDto) throws IOException {
    Invoice invoice =
        calculateInvoiceTotal(
            Usage.builder().sms(usageDto.getSms()).minutes(usageDto.getMinutes()).build(),
            priceList,
            usageDto.getPack());
    PDDocument document = new PDDocument();

    PDPage page = new PDPage(PDRectangle.A6);
    document.addPage(page);

    // Create a new font object selecting one of the PDF base fonts
    PDFont font = PDType1Font.HELVETICA;

    // Start a new content stream which will "hold" the to be created content
    PDPageContentStream contentStream = new PDPageContentStream(document, page);

    // Define a text content stream using the selected font, moving the cursor and drawing the text
    contentStream.beginText();
    contentStream.setFont(font, 12);
    contentStream.newLineAtOffset(20, page.getMediaBox().getHeight() - 100);
    for (InvoiceEntry entry : invoice.getEntries()) {
      contentStream.showText(entry.getDescription());
      contentStream.newLineAtOffset(page.getMediaBox().getWidth() - 100, 0);
      contentStream.showText(formatCurrency(entry.getValue()));
      contentStream.newLineAtOffset(-(page.getMediaBox().getWidth() - 100), -30);
    }
    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
    contentStream.newLineAtOffset(0, -20);
    contentStream.showText("TOTAL");
    contentStream.newLineAtOffset(page.getMediaBox().getWidth() - 100, 0);
    contentStream.showText(formatCurrency(invoice.getTotal()));
    contentStream.endText();

    // Make sure that the content stream is closed:
    contentStream.close();

    // Save the results and ensure that the document is properly closed:
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    document.save(byteArrayOutputStream);
    document.close();
    InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    return IOUtils.toByteArray(inputStream);
  }
}
