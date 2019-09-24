package no.nav.dolly.domain.resultset.tpsf;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsTpsfUtvidetBestilling extends RsTpsfBasisBestilling {

    private RsSimpleRelasjoner relasjoner;

    private List<RsIdenthistorikk> identhistorikk;

    List<RsRelasjon> barn;

    private String identtype;

    private LocalDateTime foedtEtter;

    private LocalDateTime foedtFoer;

    private String kjonn;
}
