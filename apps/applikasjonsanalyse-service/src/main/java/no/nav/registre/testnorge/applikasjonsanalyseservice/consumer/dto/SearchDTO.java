package no.nav.registre.testnorge.applikasjonsanalyseservice.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Builder
@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class SearchDTO {
    Integer total_count;
    Boolean incomplete_results;
    List<ItemDTO> items;
}
