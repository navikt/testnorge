package no.nav.testnav.libs.dto.arena.testnorge.tilleggsstoenad.reiseobligatorisksamling;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReiseObligatoriskSamling {

    @JsonAlias({ "KODE", "kode" })
    private ObligatoriskSamlingKoder kode;

    @JsonAlias({ "OVERORDNET", "overordnet" })
    private ObligatoriskSamlingOvKoder overordnet;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
