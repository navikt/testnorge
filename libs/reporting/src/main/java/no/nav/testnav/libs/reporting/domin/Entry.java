package no.nav.testnav.libs.reporting.domin;

import lombok.Value;

import java.time.LocalDateTime;

import no.nav.testnav.libs.dto.rapprtering.v1.EntryStatus;

@Value
public class Entry {
    EntryStatus status;
    String description;
    LocalDateTime time;
}
