package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TjenesteomraadeDTO implements Serializable {

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<String> tjenesteoppgave;
    private String tjenestevirksomhet;
}

