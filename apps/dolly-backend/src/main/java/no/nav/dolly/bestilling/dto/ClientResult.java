package no.nav.dolly.bestilling.dto;

import no.nav.dolly.domain.jpa.BestillingProgress;

public record ClientResult(String client, String status, BestillingProgress progress) {
}
