package no.nav.testnav.apps.syntaaregservice.consumer.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.apps.syntaaregservice.util.JsonDateDeserializer;
import no.nav.testnav.apps.syntaaregservice.util.JsonDateSerializer;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RsAaregOppdaterRequest extends RsAaregOpprettRequest {

    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private LocalDateTime rapporteringsperiode;
}
