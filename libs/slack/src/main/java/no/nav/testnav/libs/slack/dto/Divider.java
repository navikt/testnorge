package no.nav.testnav.libs.slack.dto;

import lombok.Value;

@Value
public class Divider implements Block {
    String type = "divider";
}
