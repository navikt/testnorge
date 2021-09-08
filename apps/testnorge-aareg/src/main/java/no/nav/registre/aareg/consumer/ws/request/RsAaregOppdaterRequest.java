package no.nav.registre.aareg.consumer.ws.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import no.nav.registre.aareg.util.JsonDateDeserializer;
import no.nav.registre.aareg.util.JsonDateSerializer;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RsAaregOppdaterRequest extends RsAaregOpprettRequest {

    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private LocalDateTime rapporteringsperiode;
}
