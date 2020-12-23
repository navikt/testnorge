package no.nav.registre.orgnrservice.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.orgnrservice.domain.Organisasjon;
import no.nav.registre.orgnrservice.repository.OrganisasjonRepository;
import no.nav.registre.orgnrservice.repository.model.OrganisasjonModel;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrganisasjonAdapter {

    private final OrganisasjonRepository organisasjonRepoistory;

    public Organisasjon save(Organisasjon organisasjon) {
        log.info("Lagrer orgnummer {}", organisasjon.getOrgnummer());
        OrganisasjonModel orgFraDb = organisasjonRepoistory.findByOrgnummer(organisasjon.getOrgnummer());

        OrganisasjonModel organisasjonModel = organisasjonRepoistory.save(
                OrganisasjonModel.builder()
                        .orgnummer(organisasjon.getOrgnummer())
                        .ledig(organisasjon.isLedig())
                        .id(orgFraDb == null ? null : orgFraDb.getId())
                        .build()
        );
        return new Organisasjon(organisasjonModel);
    }

    public List<Organisasjon> saveAll(List<Organisasjon> organisasjoner) {
        return organisasjoner.stream().map(this::save).collect(Collectors.toList());
    }

    public void deleteByOrgnummer(String orgnummer) {
        Organisasjon orgFraDb = hentByOrgnummer(orgnummer);
        if (orgFraDb == null) {
            return;
        }
        log.info("Sletter orgnummer {}", orgnummer);
        organisasjonRepoistory.deleteByOrgnummer(orgnummer);
    }

    public List<Organisasjon> hentAlleLedige() {
        log.info("Henter alle ledige orgnr...");
        var orgModeller = organisasjonRepoistory.findAllByLedigIsTrue();
        return orgModeller.stream().map(Organisasjon::new).collect(Collectors.toList());
    }

    public Organisasjon hentByOrgnummer(String orgnummer) {
        OrganisasjonModel model = organisasjonRepoistory.findByOrgnummer(orgnummer);
        return model == null ? null : new Organisasjon(model);
    }
}
