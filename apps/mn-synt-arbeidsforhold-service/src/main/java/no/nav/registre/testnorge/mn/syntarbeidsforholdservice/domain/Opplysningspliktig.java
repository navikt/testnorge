package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.OpplysningspliktigDTO;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.PersonDTO;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.VirksomhetDTO;

@Slf4j
@DependencyOn("arbeidsforhold-api")
@RequiredArgsConstructor
public class Opplysningspliktig {
    private final OpplysningspliktigDTO dto;
    private final Random random = new Random();

    public void addArbeidsforhold(Arbeidsforhold arbeidsforhold) {
        VirksomhetDTO virksomhetDTO = dto.getVirksomheter().get(random.nextInt(dto.getVirksomheter().size()));
        virksomhetDTO.getPersoner().add(new PersonDTO(arbeidsforhold.getIdent(), Collections.singletonList(
                arbeidsforhold.toDefaultDTO()
        )));
    }

    public void setKalendermaaned(LocalDate kalendermaaned){
        dto.setKalendermaaned(kalendermaaned);
    }

    public Set<String> getIdenter() {
        Set<String> identer = new HashSet<>();
        dto.getVirksomheter()
                .forEach(virksomhet -> virksomhet.getPersoner().forEach(person -> identer.add(person.getIdent())));
        return identer;
    }

    public OpplysningspliktigDTO toDTO() {
        return dto;
    }

}
