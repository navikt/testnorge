package no.nav.pdl.forvalter.mapper;

import no.nav.pdl.forvalter.dto.PdlVergemaal.Tjenesteomraade;
import no.nav.testnav.libs.dto.pdlforvalter.v1.TjenesteomraadeDTO;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VergemaalMappingStrategyTest {

    @Test
    void shouldMapNullListToEmptyList() {
        List<Tjenesteomraade> result = VergemaalMappingStrategy.mapTjenesteomraade(null);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldMapEmptyListToEmptyList() {
        List<Tjenesteomraade> result = VergemaalMappingStrategy.mapTjenesteomraade(Collections.emptyList());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldMapSingleTjenesteomraadeWithMultipleTjenesteoppgaver() {
        TjenesteomraadeDTO dto = new TjenesteomraadeDTO();
        dto.setTjenesteoppgave(List.of("hjelpemidler", "pensjon", "arbeid"));
        dto.setTjenestevirksomhet("nav");

        List<Tjenesteomraade> result = VergemaalMappingStrategy.mapTjenesteomraade(List.of(dto));

        assertThat(result).containsExactlyInAnyOrder(
                Tjenesteomraade.builder().tjenesteoppgave("hjelpemidler").tjenestevirksomhet("nav").build(),
                Tjenesteomraade.builder().tjenesteoppgave("pensjon").tjenestevirksomhet("nav").build(),
                Tjenesteomraade.builder().tjenesteoppgave("arbeid").tjenestevirksomhet("nav").build()
        );
    }

    @Test
    void shouldMapSingleTjenesteomraadeWithSingleTjenesteoppgave() {
        TjenesteomraadeDTO dto = new TjenesteomraadeDTO();
        dto.setTjenesteoppgave(List.of("hjelpemidler"));
        dto.setTjenestevirksomhet("nav");

        List<Tjenesteomraade> result = VergemaalMappingStrategy.mapTjenesteomraade(List.of(dto));

        assertThat(result).containsExactly(
                Tjenesteomraade.builder().tjenesteoppgave("hjelpemidler").tjenestevirksomhet("nav").build()
        );
    }

    @Test
    void shouldMapTjenesteomraadeWithNullTjenesteoppgaveToEmptyList() {
        TjenesteomraadeDTO dto = new TjenesteomraadeDTO();
        dto.setTjenesteoppgave(null);
        dto.setTjenestevirksomhet("nav");

        List<Tjenesteomraade> result = VergemaalMappingStrategy.mapTjenesteomraade(List.of(dto));

        assertThat(result).isEmpty();
    }

    @Test
    void shouldMapTjenesteomraadeWithEmptyTjenesteoppgaveListToEmptyList() {
        TjenesteomraadeDTO dto = new TjenesteomraadeDTO();
        dto.setTjenesteoppgave(Collections.emptyList());
        dto.setTjenestevirksomhet("nav");

        List<Tjenesteomraade> result = VergemaalMappingStrategy.mapTjenesteomraade(List.of(dto));

        assertThat(result).isEmpty();
    }

    @Test
    void shouldMapMultipleTjenesteomraader() {
        TjenesteomraadeDTO dto1 = new TjenesteomraadeDTO();
        dto1.setTjenesteoppgave(List.of("hjelpemidler", "pensjon"));
        dto1.setTjenestevirksomhet("nav");

        TjenesteomraadeDTO dto2 = new TjenesteomraadeDTO();
        dto2.setTjenesteoppgave(List.of("arbeid"));
        dto2.setTjenestevirksomhet("annen");

        List<Tjenesteomraade> result = VergemaalMappingStrategy.mapTjenesteomraade(List.of(dto1, dto2));

        assertThat(result).containsExactlyInAnyOrder(
                Tjenesteomraade.builder().tjenesteoppgave("hjelpemidler").tjenestevirksomhet("nav").build(),
                Tjenesteomraade.builder().tjenesteoppgave("pensjon").tjenestevirksomhet("nav").build(),
                Tjenesteomraade.builder().tjenesteoppgave("arbeid").tjenestevirksomhet("annen").build()
        );
    }
}
