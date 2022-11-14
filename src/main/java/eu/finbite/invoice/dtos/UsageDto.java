package eu.finbite.invoice.dtos;

import eu.finbite.invoice.models.Package;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Validated
public class UsageDto {

    @Min(value = 0L, message = "The value must be positive")
    private Integer minutes;
    @Min(value = 0L, message = "The value must be positive")
    private Integer sms;
    @NotNull
    private Package pack;
}
