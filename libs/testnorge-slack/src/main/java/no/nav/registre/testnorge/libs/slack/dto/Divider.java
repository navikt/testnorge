package no.nav.registre.testnorge.libs.slack.dto;

import lombok.Value;

@Value
public class Divider implements Block {
    String type = "divider";
}
