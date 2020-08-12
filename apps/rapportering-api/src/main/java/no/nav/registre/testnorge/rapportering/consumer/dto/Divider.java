package no.nav.registre.testnorge.rapportering.consumer.dto;

import lombok.Value;

@Value
public class Divider implements Block {
    String type = "divider";
}
