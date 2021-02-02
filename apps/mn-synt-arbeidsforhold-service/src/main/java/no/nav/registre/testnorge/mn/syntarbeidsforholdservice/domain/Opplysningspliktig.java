package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.ArbeidsforholdDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.PersonDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.VirksomhetDTO;

@Slf4j
@DependencyOn("testnorge-arbeidsforhold-api")
@RequiredArgsConstructor
public class Opplysningspliktig {
    private static final Random RANDOM = new Random();
    private final OppsummeringsdokumentDTO dto;
    private final List<String> driverVirksomheter;
    private boolean changed = false;

    public Opplysningspliktig(Organisajon organisajon, LocalDate kalendermaand) {
        driverVirksomheter = organisajon.getDriverVirksomheter();
        dto = OppsummeringsdokumentDTO
                .builder()
                .version(1L)
                .kalendermaaned(kalendermaand)
                .opplysningspliktigOrganisajonsnummer(organisajon.getOrgnummer())
                .virksomheter(new ArrayList<>())
                .build();
    }

    public String getRandomVirksomhetsnummer(){
        return driverVirksomheter.get(RANDOM.nextInt(driverVirksomheter.size()));
    }

    public boolean driverVirksomhet(String virksomhetesnummer){
        return driverVirksomheter.stream().anyMatch(value -> value.equals(virksomhetesnummer));
    }


    public List<VirksomhetDTO> getDriverVirksomheter() {
        return dto.getVirksomheter();
    }

    //TODO: Fiks muting av object
    public void addArbeidsforhold(Arbeidsforhold arbeidsforhold) {
        changed = true;
        String virksomhetsnummer = arbeidsforhold.getVirksomhetsnummer();
        VirksomhetDTO virksomhet = dto.getVirksomheter()
                .stream()
                .filter(value -> value.getOrganisajonsnummer().equals(virksomhetsnummer))
                .findFirst().orElseGet(() -> {
                    VirksomhetDTO virksomhetDTO = new VirksomhetDTO(virksomhetsnummer, new ArrayList<>());
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
                    .filter(arbeidsforholdDTO -> arbeidsforholdDTO.getArbeidsforholdId().equals(arbeidsforhold.getArbeidsforholdId()))
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

    public String getOrgnummer(){
        return dto.getOpplysningspliktigOrganisajonsnummer();
    }

    public LocalDate getKalendermaaned(){
        return dto.getKalendermaaned();
    }

    public void setKalendermaaned(LocalDate kalendermaaned) {
        dto.setKalendermaaned(kalendermaaned);
    }

    public void setVersion(Long version) {
        dto.setVersion(version);
    }

    public Long getVersion() {
        return dto.getVersion() == null ? 0 : dto.getVersion();
    }

    public OppsummeringsdokumentDTO toDTO() {
        return dto;
    }

    public boolean isChanged(){
        return changed;
    }

}
