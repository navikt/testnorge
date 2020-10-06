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

import no.nav.registre.testnorge.libs.dto.rapprtering.v1.EntryStatus;
import no.nav.registre.testnorge.libs.slack.dto.ActionAttachment;
import no.nav.registre.testnorge.libs.slack.dto.Attachment;
import no.nav.registre.testnorge.libs.slack.dto.Block;
import no.nav.registre.testnorge.libs.slack.dto.ButtonAction;
import no.nav.registre.testnorge.libs.slack.dto.Divider;
import no.nav.registre.testnorge.libs.slack.dto.Message;
import no.nav.registre.testnorge.libs.slack.dto.Section;
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
    String traceId;

    public Report(ReportModel model) {
        id = model.getId();
        applicationName = model.getApplicationName();
        name = model.getName();
        start = model.getStart().toLocalDateTime();
        end = model.getEnd().toLocalDateTime();
        entries = model.getEntrys().stream().map(Entry::new).collect(Collectors.toList());
        traceId = model.getTraceId();
    }


    public Report(no.nav.registre.testnorge.libs.avro.report.Report report) {
        id = null;
        applicationName = report.getApplicationName().toString();
        name = report.getName().toString();
        start = LocalDateTime.parse(report.getStart());
        end = LocalDateTime.parse(report.getEnd());
        entries = report.getEntries() != null
                ? report.getEntries().stream().map(Entry::new).collect(Collectors.toList())
                : Collections.emptyList();
        traceId = report.getTraceId() != null ? report.getTraceId().toString() : null;
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
                .traceId(traceId)
                .build();
    }

    public Message toSlackMessage(String channel) {
        var headerBlock = Section.from("*Rapport fra: \"" + name + " (" + applicationName + ")\"*");
        var durationBlock = Section.from("(" + start.format(FORMATTER) + " - " + end.format(FORMATTER) + ")");
        var blocks = new ArrayList<Block>();
        var attachments = new ArrayList<Attachment>();

        blocks.add(headerBlock);
        blocks.add(durationBlock);
        blocks.addAll(entries.stream().map(
                value -> Section.from(entryToText(value))
        ).collect(Collectors.toList()));

        if (traceId != null) {
            String url = createLogUrl();
            attachments.add(new ActionAttachment("Lenke til log " + url, new ButtonAction("Se log", url)));
        } else {
            blocks.add(new Divider());
        }
        return Message
                .builder()
                .channel(channel)
                .blocks(blocks)
                .attachments(attachments)
                .build();
    }

    private String createLogUrl() {
        return "https://logs.adeo.no/app/kibana#/discover?_g=(filters:!(),refreshInterval:(pause:!t,value:0),time:(from:now-7d,to:now))&_a=(columns:!(message,envclass,level,application,host),filters:!(),interval:auto,query:(language:lucene,query:'x_traceId:" + traceId + "'),sort:!())";
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
