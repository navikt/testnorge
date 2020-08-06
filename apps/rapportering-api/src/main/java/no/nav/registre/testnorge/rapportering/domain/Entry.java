package no.nav.registre.testnorge.rapportering.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import no.nav.registre.testnorge.dto.rapprtering.v1.EntryStatus;
import no.nav.registre.testnorge.rapportering.repository.model.EntryModel;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Entry {
    EntryStatus status;
    String description;
    LocalDateTime time;

    public Entry(EntryModel model) {
        status = model.getStatus();
        description = model.getDescription();
        time = model.getTime().toLocalDateTime();
    }

    public EntryModel toModel() {
        return EntryModel
                .builder()
                .status(status)
                .time(Timestamp.valueOf(time))
                .description(description)
                .build();
    }

    public Entry(no.nav.registre.testnorge.avro.report.Entry entry) {
        status = EntryStatus.valueOf(entry.getStatus().toString());
        description = entry.getDescription().toString();
        time = LocalDateTime.parse(entry.getTime());
    }
}
