package no.nav.dolly.domain.resultset.udistub.model;

import java.time.ZonedDateTime;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.util.JsonZonedDateTimeDeserializer;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UdiPeriode {
    @JsonDeserialize(using = JsonZonedDateTimeDeserializer.class)
    private ZonedDateTime fra;
    @JsonDeserialize(using = JsonZonedDateTimeDeserializer.class)
    private ZonedDateTime til;
}
