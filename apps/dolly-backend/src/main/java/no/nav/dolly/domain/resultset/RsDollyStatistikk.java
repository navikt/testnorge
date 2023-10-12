package no.nav.dolly.domain.resultset;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RsDollyStatistikk(Long antallBestillinger, Long antallIdenter) {
};
