package no.nav.registre.testnorge.libs.reporting.domin;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class Report {
    String name;
    LocalDateTime start;
    LocalDateTime end;
    List<Entry> entries;
}
