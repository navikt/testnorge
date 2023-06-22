package no.nav.testnav.apps.apptilganganalyseservice.domain;

import lombok.ToString;
import lombok.Value;
import no.nav.testnav.apps.apptilganganalyseservice.consumer.dto.ItemDTO;

@Value
@ToString
public class ItemResult {

    String sha;
    String path;
    public ItemResult(ItemDTO dto) {
        this.sha = dto.getSha();
        this.path = dto.getPath();
    }
}
