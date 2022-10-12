package no.nav.dolly.service.excel.dto;

import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonDetaljer;

public record OrganisasjonDTO(String hierarki, OrganisasjonDetaljer organisasjon) {
}
