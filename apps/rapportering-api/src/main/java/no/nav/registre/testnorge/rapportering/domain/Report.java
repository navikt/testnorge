package no.nav.registre.testnorge.rapportering.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.dto.rapprtering.v1.EntryStatus;
import no.nav.registre.testnorge.rapportering.consumer.dto.Block;
import no.nav.registre.testnorge.rapportering.consumer.dto.Message;
import no.nav.registre.testnorge.rapportering.repository.model.ReportModel;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Report {
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    Long id;
    String applicationName;
    String name;
    LocalDateTime start;
    LocalDateTime end;
    List<Entry> entries;

    public Report(ReportModel model) {
        id = model.getId();
        applicationName = model.getApplicationName();
        name = model.getName();
        start = model.getStart().toLocalDateTime();
        end = model.getEnd().toLocalDateTime();
        entries = model.getEntrys().stream().map(Entry::new).collect(Collectors.toList());
    }


    public Report(no.nav.registre.testnorge.avro.report.Report report) {
        id = null;
        applicationName = report.getApplicationName().toString();
        name = report.getName().toString();
        start = LocalDateTime.parse(report.getStart());
        end = LocalDateTime.parse(report.getEnd());
        entries = report.getEntries() != null
                ? report.getEntries().stream().map(Entry::new).collect(Collectors.toList())
                : Collections.emptyList();
    }

    public ReportModel toModel() {
        return ReportModel
                .builder()
                .applicationName(applicationName)
                .name(name)
                .start(Timestamp.valueOf(start))
                .end(Timestamp.valueOf(end))
                .entrys(entries != null
                        ? entries.stream().map(Entry::toModel).collect(Collectors.toList())
                        : Collections.emptyList()
                )
                .build();
    }

    public Message toSlackMessage(String channel) {
        var headerBlock = Block.from("*Rapport fra " + name + " (" + applicationName + ")*");
        var durationBlock = Block.from("(" + start.format(FORMATTER) + " - " + end.format(FORMATTER) + ")");
        var blocks = new ArrayList<Block>();

        blocks.add(headerBlock);
        blocks.add(durationBlock);
        blocks.addAll(entries.stream().map(
                value -> Block.from(entryToText(value))
        ).collect(Collectors.toList()));
        blocks.add(new Block("divider", null));
        return Message
                .builder()
                .channel(channel)
                .blocks(blocks)
                .build();
    }

    private String entryToText(Entry entry) {
        return getIcon(entry.getStatus()) + " " + entry.getDescription();
    }

    private String getIcon(EntryStatus status) {
        switch (status) {
            case ERROR:
                return ":X:";
            case WARNING:
                return ":warning:";
            case INFO:
            default:
                return ":white_check_mark:";
        }
    }
}
