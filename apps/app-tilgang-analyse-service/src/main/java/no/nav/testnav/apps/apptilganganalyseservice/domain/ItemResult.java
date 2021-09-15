package no.nav.testnav.apps.apptilganganalyseservice.domain;

import lombok.Value;

import no.nav.testnav.apps.apptilganganalyseservice.consumer.dto.ItemDTO;

@Value
public class ItemResult {

    public ItemResult(ItemDTO dto) {
        this.sha = dto.getSha();
        this.path = dto.getPath();
    }

    String sha;
    String path;
}
