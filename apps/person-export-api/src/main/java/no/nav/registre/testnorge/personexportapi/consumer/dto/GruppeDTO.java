package no.nav.registre.testnorge.personexportapi.consumer.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class GruppeDTO {
    String id;
    String navn;
    List<EndringsmeldingDTO> meldinger;
    Integer antallSider;
}
