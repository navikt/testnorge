package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.deserializer.StringOrListDeserializer;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TjenesteomraadeDTO implements Serializable {

    @JsonDeserialize(using = StringOrListDeserializer.class)
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<String> tjenesteoppgave;
    private String tjenestevirksomhet;
}

