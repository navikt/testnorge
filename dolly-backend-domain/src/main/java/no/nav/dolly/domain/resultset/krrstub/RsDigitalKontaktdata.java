package no.nav.dolly.domain.resultset.krrstub;

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
public class RsDigitalKontaktdata {
    @JsonDeserialize(using = JsonZonedDateTimeDeserializer.class)
    private ZonedDateTime gyldigFra;
    private boolean reservert;
    private String mobil;
    private String epost;
    private boolean registrert;
}