package no.nav.dolly.service.excel;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.consumer.pdlperson.dto.PdlBolkResponse;
import no.nav.dolly.consumer.pdlperson.dto.PdlPersonDTO;
import no.nav.dolly.util.IdentTypeUtil;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdsadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalDTO;
import org.springframework.stereotype.Service;
import wiremock.com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class PdlExcelService extends PdlPersonExcelService {

    private final PdlPersonConsumer pdlPersonConsumer;

    private static String getFullmektig(List<FullmaktDTO> fullmakt) {

        return fullmakt.stream()
                .map(FullmaktDTO::getMotpartsPersonident)
                .collect(Collectors.joining(",\n"));
    }

    private static String getVerge(List<VergemaalDTO> vergemaal) {

        return vergemaal.stream()
                .map(VergemaalDTO::getVergeIdent)
                .collect(Collectors.joining(",\n"));
    }

    private static String getForeldre(List<PdlPersonDTO.ForelderBarnRelasjon> foreldre) {

        return foreldre.stream()
                .filter(forelder -> ForelderBarnRelasjonDTO.Rolle.BARN == forelder.getMinRolleForPerson())
                .map(PdlPersonDTO.ForelderBarnRelasjon::getRelatertPersonsIdent)
                .collect(Collectors.joining(",\n"));
    }

    private static String getBarn(List<PdlPersonDTO.ForelderBarnRelasjon> barn) {

        return barn.stream()
                .filter(barnet -> ForelderBarnRelasjonDTO.Rolle.BARN == barnet.getRelatertPersonsRolle())
                .map(PdlPersonDTO.ForelderBarnRelasjon::getRelatertPersonsIdent)
                .collect(Collectors.joining(",\n"));
    }

    private static String getPartnere(List<SivilstandDTO> partnere) {

        return partnere.stream()
                .filter(partner -> nonNull(partner.getRelatertVedSivilstand()))
                .map(SivilstandDTO::getRelatertVedSivilstand)
                .collect(Collectors.joining(",\n"));
    }

    public List<Object[]> getFormattedCellsFromPdl(List<String> identer) {

        return Lists.partition(identer, 10).stream()
                .map(personer -> pdlPersonConsumer.getPdlPersoner(personer))
                .map(PdlBolkResponse::getData)
                .map(PdlBolkResponse.Contents::getHentPersonBolk)
                .flatMap(Collection::stream)
                .filter(bolkPerson -> nonNull(bolkPerson.getPerson()))
                .map(person -> new Object[]{
                        person.getIdent(),
                        IdentTypeUtil.getIdentType(person.getIdent()).name(),
                        getFornavn(person.getPerson().getNavn().stream().findFirst().orElse(null)),
                        getEtternavn(person.getPerson().getNavn().stream().findFirst().orElse(null)),
                        getAlder(person.getIdent(), person.getPerson().getDoedsfall().isEmpty() ? null :
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
                        getPartnere(person.getPerson().getSivilstand()),
                        getBarn(person.getPerson().getForelderBarnRelasjon()),
                        getForeldre(person.getPerson().getForelderBarnRelasjon()),
                        getVerge(person.getPerson().getVergemaal()),
                        getFullmektig(person.getPerson().getFullmakt())
                })
                .toList();
    }
}
