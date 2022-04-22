package no.nav.registre.testnorge.personsearchservice.domain;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.personsearchservice.adapter.model.ForelderBarnRelasjonModel;
import no.nav.testnav.libs.dto.personsearchservice.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.personsearchservice.v1.ForelderDTO;

import java.util.List;

@RequiredArgsConstructor
public class ForelderBarnRelasjon implements WithDTO<ForelderBarnRelasjonDTO> {
    private final List<ForelderBarnRelasjonModel> forelderBarnRelasjoner;

    public ForelderBarnRelasjonDTO toDTO() {
        var barn = forelderBarnRelasjoner.stream()
                .filter(relasjon -> relasjon.getRelatertPersonsRolle().equals(PersonRolle.BARN.toString()))
                .map(ForelderBarnRelasjonModel::getRelatertPersonsIdent)
                .toList();

        var foreldre = forelderBarnRelasjoner.stream()
                .filter(relasjon -> !relasjon.getRelatertPersonsRolle().equals(PersonRolle.BARN.toString()))
                .map(forelder -> {
                    return ForelderDTO.builder()
                            .ident(forelder.getRelatertPersonsIdent())
                            .rolle(forelder.getRelatertPersonsRolle())
                            .build();
                })
                .toList();
        return ForelderBarnRelasjonDTO.builder()
                .barn(barn)
                .foreldre(foreldre)
                .build();
    }

}
