package no.nav.registre.testnorge.personexportapi.consumer.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class GruppeDTO {
    String id;
    String navn;
    List<EndringsmeldingDTO> meldinger;
    Integer antallSider;

    @JsonIgnore
    public int size() {
        return meldinger == null ? 0 : meldinger.size();
    }
}
