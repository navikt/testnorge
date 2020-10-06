package no.nav.registre.testnorge.tilbakemeldingapi.domain;

import java.util.ArrayList;

import no.nav.registre.testnorge.libs.dto.tilbakemeldingapi.v1.Rating;
import no.nav.registre.testnorge.libs.dto.tilbakemeldingapi.v1.TilbakemeldingDTO;
import no.nav.registre.testnorge.libs.slack.dto.Attachment;
import no.nav.registre.testnorge.libs.slack.dto.Block;
import no.nav.registre.testnorge.libs.slack.dto.Divider;
import no.nav.registre.testnorge.libs.slack.dto.Message;
import no.nav.registre.testnorge.libs.slack.dto.Section;
import no.nav.registre.testnorge.libs.slack.dto.TextAttachment;

public class Tilbakemelding {
    private final String title;
    private final String message;
    private final Rating rating;

    public Tilbakemelding(TilbakemeldingDTO dto) {
        rating = dto.getRating();
        title = dto.getTitle();
        message = dto.getMessage();
    }

    public Message toSlackMessage(String channel) {
        var ratingIcon = (rating != null ? " " + getIcon(rating) : "");

        var headerBlock = Section.from(
                "*Tilbakemelding for: " + title + "*" + ratingIcon
        );

        var blocks = new ArrayList<Block>();
        blocks.add(headerBlock);
        blocks.add(new Divider());

        var attachments = new ArrayList<Attachment>();
        attachments.add(new TextAttachment(message));

        return Message
                .builder()
                .channel(channel)
                .blocks(blocks)
                .attachments(attachments)
                .build();
    }

    private String getIcon(Rating rating) {
        switch (rating) {
            case POSITIVE:
                return ":thumbsup:";
            case NEGATIVE:
                return ":thumbsdown:";
            default:
                return "";
        }
    }
}
