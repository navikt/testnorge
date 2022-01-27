package no.nav.dolly.service.excel;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.util.IdentTypeUtil;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdsadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.springframework.stereotype.Service;
import wiremock.com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PdlForvalterExcelService extends PdlPersonExcelService {

    private final PdlDataConsumer pdlDataConsumer;

    public List<Object[]> getFormattetCellsFromPdlForvalter(List<String> identer) {

        return Lists.partition(identer, 10).stream()
                .map(personer -> pdlDataConsumer.getPersoner(personer))
                .flatMap(Collection::stream)
                .map(person -> new Object[]{
                        person.getPerson().getIdent(),
                        IdentTypeUtil.getIdentType(person.getPerson().getIdent()).name(),
                        getFornavn(person.getPerson().getNavn().stream().findFirst().orElse(null)),
                        getEtternavn(person.getPerson().getNavn().stream().findFirst().orElse(null)),
                        getAlder(person.getPerson().getIdent(), person.getPerson().getDoedsfall().isEmpty() ? null :
                                toLocalDate(person.getPerson().getDoedsfall().stream().findFirst().get().getDoedsdato())),
                        getKjoenn(person.getPerson().getKjoenn().stream().findFirst().orElse(null)),
                        getDoedsdato(person.getPerson().getDoedsfall().stream().findFirst().orElse(null)),
                        getPersonstatus(person.getPerson().getFolkeregisterPersonstatus().stream().findFirst().orElse(null)),
                        getStatsborgerskap(person.getPerson().getStatsborgerskap().stream().findFirst().orElse(null)),
                        getAdressebeskyttelse(person.getPerson().getAdressebeskyttelse().stream().findFirst().orElse(null)),
                        getBoadresse(person.getPerson().getBostedsadresse().stream().findFirst().orElse(new BostedadresseDTO())),
                        getKontaktadresse(person.getPerson().getKontaktadresse().stream().findFirst().orElse(new KontaktadresseDTO())),
                        getOppholdsadresse(person.getPerson().getOppholdsadresse().stream().findFirst().orElse(new OppholdsadresseDTO())),
                        getSivilstand(person.getPerson().getSivilstand().stream().findFirst().orElse(null)),
                        getPersonRelasjon(person.getRelasjoner().stream()
                                .filter(relasjon -> RelasjonType.EKTEFELLE_PARTNER == relasjon.getRelasjonType())
                                .map(FullPersonDTO.RelasjonDTO::getRelatertPerson)
                                .toList()),
                        getPersonRelasjon(person.getRelasjoner().stream()
                                .filter(relasjon -> RelasjonType.FAMILIERELASJON_BARN == relasjon.getRelasjonType())
                                .map(FullPersonDTO.RelasjonDTO::getRelatertPerson)
                                .toList()),
                        getPersonRelasjon(person.getRelasjoner().stream()
                                .filter(relasjon -> RelasjonType.FAMILIERELASJON_FORELDER == relasjon.getRelasjonType())
                                .map(FullPersonDTO.RelasjonDTO::getRelatertPerson)
                                .toList()),
                        getPersonRelasjon(person.getRelasjoner().stream()
                                .filter(relasjon -> RelasjonType.VERGE == relasjon.getRelasjonType())
                                .map(FullPersonDTO.RelasjonDTO::getRelatertPerson)
                                .toList()),
                        getPersonRelasjon(person.getRelasjoner().stream()
                                .filter(relasjon -> RelasjonType.FULLMEKTIG == relasjon.getRelasjonType())
                                .map(FullPersonDTO.RelasjonDTO::getRelatertPerson)
                                .toList())
                })
                .toList();
    }

    private String getPersonRelasjon(List<PersonDTO> barna) {

        return barna.stream()
                .map(barn ->
                        String.format("%s -- %s %s", barn.getIdent(),
                                getFornavn(barn.getNavn().stream().findFirst().orElse(null)),
                                barn.getNavn().stream().findFirst().orElse(new NavnDTO()).getEtternavn()))
                .collect(Collectors.joining(",\n"));
    }
}
