package no.nav.registre.testnorge.personsearchservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Page {
    Integer page;
    Integer pageSize;
}