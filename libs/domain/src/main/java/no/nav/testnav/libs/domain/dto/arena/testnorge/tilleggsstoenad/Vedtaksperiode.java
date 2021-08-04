package no.nav.testnav.libs.domain.dto.arena.testnorge.tilleggsstoenad;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vedtaksperiode {

    @JsonAlias({ "FOM", "fom" })
    private LocalDate fom;

    @JsonAlias({ "TOM", "tom" })
    private LocalDate tom;
}
