package no.nav.registre.testnorge.libs.reporting.domin;

import lombok.Value;

import java.time.LocalDateTime;

import no.nav.registre.testnorge.libs.dto.rapprtering.v1.EntryStatus;

@Value
public class Entry {
    EntryStatus status;
    String description;
    LocalDateTime time;
}
