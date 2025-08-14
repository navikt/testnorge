package no.nav.registre.testnorge.tilbakemeldingapi.domain;

import lombok.Value;
import no.nav.testnav.libs.dto.tilbakemeldingapi.v1.Rating;
import no.nav.testnav.libs.dto.tilbakemeldingapi.v1.TilbakemeldingDTO;
import no.nav.testnav.libs.slack.dto.Attachment;
import no.nav.testnav.libs.slack.dto.Block;
import no.nav.testnav.libs.slack.dto.Divider;
import no.nav.testnav.libs.slack.dto.Message;
import no.nav.testnav.libs.slack.dto.Section;
import no.nav.testnav.libs.slack.dto.TextAttachment;

import java.util.ArrayList;

import static java.util.Objects.nonNull;

@Value
public class Tilbakemelding {
    String title;
    String message;
    Rating rating;
    Boolean isAnonym;
    String brukerType;
    String brukernavn;
    String tilknyttetOrganisasjon;

    public Tilbakemelding(TilbakemeldingDTO dto) {
        rating = dto.getRating();
        title = dto.getTitle();
        message = dto.getMessage();
        isAnonym = dto.getIsAnonym();
        brukerType = dto.getBrukerType();
        brukernavn = dto.getBrukernavn();
        tilknyttetOrganisasjon = dto.getTilknyttetOrganisasjon();

    }

    public Message toSlackMessage(String channel, String visningsNavn) {
        var ratingIcon = (rating != null ? " " + getIcon(rating) : "");
        var brukerTypeString = (nonNull(brukerType) && brukerType.toUpperCase().contains("AZURE") ? " (" + brukerType + ")" : "");

        var headerBlock = Section.from(
                "*" + title + "*" + ratingIcon
        );

        var senderBlock = Section.from(
                "Avsender: " + visningsNavn + brukerTypeString
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
        return switch (rating) {
            case POSITIVE -> ":thumbsup:";
            case NEGATIVE -> ":thumbsdown:";
        };
    }
}
