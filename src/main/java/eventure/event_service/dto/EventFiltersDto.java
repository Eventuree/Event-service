package eventure.event_service.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class EventFiltersDto {
    private String keyword;
    private Integer categoryId;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateTo;

    private String location;

    @Min(0)
    private Short minAge;

    @Min(0)
    private Short maxAge;

    @AssertTrue(message = "dateFrom must be before or equal to dateTo")
    public boolean isDateRangeValid() {
        if (dateFrom == null || dateTo == null) {
            return true;
        }
        return !dateFrom.isAfter(dateTo);
    }

    @AssertTrue(message = "minAge must be less than or equal to maxAge")
    public boolean isAgeRangeValid() {
        if (minAge == null || maxAge == null) {
            return true;
        }
        return minAge <= maxAge;
    }
}
