package no.nav.dolly.domain.resultset.tpsf;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsTpsfUtvidetBestilling extends RsTpsfBasisBestilling {

    List<RsRelasjon> barn;
    private RsSimpleRelasjoner relasjoner;
    private String identtype;

    private LocalDateTime foedtEtter;

    private LocalDateTime foedtFoer;

    private String kjonn;
}
