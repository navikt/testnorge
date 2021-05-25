package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.Id;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.ArbeidsforholdDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.PersonDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.VirksomhetDTO;

@Slf4j
public class Oppsummeringsdokument implements Id {
    private final OppsummeringsdokumentDTO dto;

    public Oppsummeringsdokument(OppsummeringsdokumentDTO dto) {
        this.dto = dto;
        this.dto.setVersion(this.dto.getVersion() == null ? 1L : this.dto.getVersion() + 1);
    }

    public String getOpplysningspliktigOrganisajonsnummer() {
        return dto.getOpplysningspliktigOrganisajonsnummer();
    }

    public LocalDate getKalendermaaned() {
        return dto.getKalendermaaned();
    }

    public void remove(Arbeidsforhold arbeidsforhold) {
        log.info("Fjerner arbeidsforhold med id {}.", arbeidsforhold.getId());
        var person = dto.getVirksomheter()
                .stream()
                .filter(value -> value.getOrganisajonsnummer().equals(arbeidsforhold.getVirksomhetsnummer()))
                .findFirst()
                .orElseThrow()
                .getPersoner()
                .stream()
                .filter(value -> value.getIdent().equals(arbeidsforhold.getIdent()))
                .findFirst()
                .orElseThrow();
        person.getArbeidsforhold().remove(arbeidsforhold.toDTO());
    }

    public void addAll(List<Arbeidsforhold> arbeidsforhold) {
        arbeidsforhold.forEach(this::add);
    }

    private void add(Arbeidsforhold arbeidsforhold) {

        VirksomhetDTO virksomhet = dto.getVirksomheter()
                .stream()
                .filter(value -> value.getOrganisajonsnummer().equals(arbeidsforhold.getVirksomhetsnummer()))
                .findFirst()
                .orElseGet(() -> {
                    VirksomhetDTO virksomhetDTO = new VirksomhetDTO(arbeidsforhold.getVirksomhetsnummer(), new ArrayList<>());
                    dto.getVirksomheter().add(virksomhetDTO);
                    return virksomhetDTO;
                });

        Optional<PersonDTO> optional = virksomhet.getPersoner()
                .stream()
                .filter(value -> value.getIdent().equals(arbeidsforhold.getIdent()))
                .findFirst();

        if (optional.isEmpty()) {
            virksomhet.getPersoner().add(new PersonDTO(arbeidsforhold.getIdent(), Collections.singletonList(
                    arbeidsforhold.toDTO()
            )));
        } else {
            PersonDTO personDTO = optional.get();
            Optional<ArbeidsforholdDTO> tidligereArbeidsforhold = personDTO.getArbeidsforhold()
                    .stream()
                    .filter(arbeidsforholdDTO -> arbeidsforholdDTO.getArbeidsforholdId().equals(arbeidsforhold.getId()))
                    .findFirst();

            List<ArbeidsforholdDTO> list = new ArrayList<>(personDTO.getArbeidsforhold());
            if (tidligereArbeidsforhold.isPresent()) {
                ArbeidsforholdDTO dto = tidligereArbeidsforhold.get();
                log.info("Fjerner tidligere arbeidsforhold {}", dto.getArbeidsforholdId());
                list.remove(dto);
            }

            list.add(arbeidsforhold.toDTO());
            personDTO.setArbeidsforhold(list);
        }
    }

    public OppsummeringsdokumentDTO toDTO() {
        return dto;
    }

    @Override
    public String getId() {
        return getOpplysningspliktigOrganisajonsnummer();
    }
}
