package no.nav.testnav.libs.slack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Message {
    String channel;
    List<Block> blocks;
    List<Attachment> attachments;
}
