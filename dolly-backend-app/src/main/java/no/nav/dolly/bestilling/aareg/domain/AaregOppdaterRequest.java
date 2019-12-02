package no.nav.dolly.bestilling.aareg.domain;

import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.aareg.util.JsonDateDeserializer;
import no.nav.dolly.domain.resultset.aareg.util.JsonDateSerializer;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AaregOppdaterRequest extends AaregOpprettRequest {

    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private LocalDateTime rapporteringsperiode;
}
