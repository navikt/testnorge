package no.nav.testnav.libs.dto.arena.testnorge.tilleggsstoenad;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vedtaksperiode {

    @JsonAlias({ "FOM", "fom" })
    private LocalDate fom;

    @JsonAlias({ "TOM", "tom" })
    private LocalDate tom;
}
