package no.nav.registre.testnorge.tilbakemeldingapi.domain;

import java.util.ArrayList;

import lombok.Value;
import no.nav.testnav.libs.dto.tilbakemeldingapi.v1.Rating;
import no.nav.testnav.libs.dto.tilbakemeldingapi.v1.TilbakemeldingDTO;
import no.nav.registre.testnorge.libs.slack.dto.Attachment;
import no.nav.registre.testnorge.libs.slack.dto.Block;
import no.nav.registre.testnorge.libs.slack.dto.Divider;
import no.nav.registre.testnorge.libs.slack.dto.Message;
import no.nav.registre.testnorge.libs.slack.dto.Section;
import no.nav.registre.testnorge.libs.slack.dto.TextAttachment;

@Value
public class Tilbakemelding {
    String title;
    String message;
    Rating rating;
    Boolean isAnonym;

    public Tilbakemelding(TilbakemeldingDTO dto) {
        rating = dto.getRating();
        title = dto.getTitle();
        message = dto.getMessage();
        isAnonym = dto.getIsAnonym();
    }

    public Message toSlackMessage(String channel, String visningsNavn) {
        var ratingIcon = (rating != null ? " " + getIcon(rating) : "");

        var headerBlock = Section.from(
                "*" + title + "*" + ratingIcon
        );

        var senderBlock = Section.from(
                "Avsender: " + visningsNavn
        );

        var blocks = new ArrayList<Block>();
        blocks.add(headerBlock);
        blocks.add(senderBlock);
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
