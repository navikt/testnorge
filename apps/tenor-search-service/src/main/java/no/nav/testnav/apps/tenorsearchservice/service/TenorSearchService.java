package no.nav.testnav.apps.tenorsearchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tenorsearchservice.consumers.TenorClient;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorRequest;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenorSearchService {

    private final TenorClient tenorClient;

    public Mono<TenorResponse> getTestdata(String testDataQuery) {

        return tenorClient.getTestdata(isNotBlank(testDataQuery) ? testDataQuery : "");
    }

    public Mono<TenorResponse> getTestdata(TenorRequest searchData) {

        var builder = new StringBuilder()
                .append(convertString("identifikator", searchData.getIdentifikator()))
                .append(convertDatoer("foedselsdato", searchData.getFoedselsdato()))
                .append(convertDatoer("doedsdato", searchData.getDoedsdato()))
                .append(getIdentifikatorType(searchData.getIdentifikatorType()))
                .append(convertEnum("kjoenn", searchData.getKjoenn()))
                .append(convertEnum("personstatus", searchData.getPersonstatus()))
                .append(convertEnum("sivilstatus", searchData.getSivilstatus()))
                .append(getUtenlandskPersonidentifikasjon(searchData.getUtenlandskPersonIdentifikasjon()))
                .append(convertEnum("identitetsgrunnlagStatus", searchData.getIdentitetsgrunnlagStatus()))
                .append(convertEnum("adressebeskyttelse", searchData.getAdressebeskyttelse()))
                .append(convertBoolean("legitimasjonsdokument", searchData.getHarLegitimasjonsdokument()))
                .append(convertBoolean("falskIdentitet", searchData.getHarFalskIdentitet()))
                .append(convertBoolean("norskStatsborgerskap", searchData.getHarNorskStatsborgerskap()))
                .append(convertBoolean("flereStatsborgerskap", searchData.getHarFlereStatsborgerskap()));

        if (nonNull(searchData.getNavn())) {
            builder.append(convertBoolean("flereFornavn", searchData.getNavn().getHarFlereFornavn()))
                    .append(getIntervall("navnLengde", searchData.getNavn().getNavnLengde()))
                    .append(convertBooleanWildcard("mellomnavn", searchData.getNavn().getHarMellomnavn()))
                    .append(convertBoolean("navnSpesialtegn", searchData.getNavn().getHarNavnSpesialtegn()));
        }

        if (nonNull(searchData.getAdresser())) {
            builder.append(convertString("bostedsadresse", searchData.getAdresser().getBostedsadresseFritekst()))
                    .append(convertBooleanWildcard("bostedsadresse", searchData.getAdresser().getHarBostedsadresse()))
                    .append(convertBooleanWildcard("oppholdAnnetSted", searchData.getAdresser().getHarOppholdAnnetSted()))
                    .append(convertBooleanWildcard("postadresse", searchData.getAdresser().getHarPostadresseNorge()))
                    .append(convertBooleanWildcard("postadresseUtland", searchData.getAdresser().getHarPostadresseUtland()))
                    .append(convertBooleanWildcard("kontaktinfoDoedsbo", searchData.getAdresser().getHarKontaktadresseDoedsbo()))
                    .append(convertBoolean("adresseSpesialtegn", searchData.getAdresser().getHarAdresseSpesialtegn()))
                    .append(convertEnum("bostedsadresseType", searchData.getAdresser().getBostedsadresseType()))
                    .append(convertEnum("coAdressenavnType", searchData.getAdresser().getCoAdresseType()));
        }

        if (nonNull(searchData.getRelasjoner())) {
            builder.append(getRelasjon(searchData.getRelasjoner().getRelasjon()))
                    .append(getIntervall("antallBarn", searchData.getRelasjoner().getAntallBarn()))
                    .append(convertBooleanWildcard("foreldreansvar", searchData.getRelasjoner().getHarForeldreAnsvar()))
                    .append(getRelasjonMedFoedselsdato(searchData.getRelasjoner().getRelasjonMedFoedselsaar()))
                    .append(convertBooleanWildcard("deltBosted", searchData.getRelasjoner().getHarDeltBosted()))
                    .append(convertBooleanWildcard("vergemaalType", searchData.getRelasjoner().getHarVergemaalEllerFremtidsfullmakt()))
                    .append(convertBoolean("borMedFar", searchData.getRelasjoner().getBorMedFar()))
                    .append(convertBoolean("borMedMor", searchData.getRelasjoner().getBorMedMor()))
                    .append(convertBoolean("borMedMedMor", searchData.getRelasjoner().getBorMedMedmor()));
        }

        if (nonNull(searchData.getHendelser())) {
            builder.append(convertEnum("hendelserMedSekvens.hendelse", searchData.getHendelser().getHendelse()))
                    .append(convertEnum("sisteHendelse", searchData.getHendelser().getSisteHendelse()));
        }
        builder.append(getRoller(searchData.getRoller()));

        log.info("Søker med query: {}", builder.substring(5));
        return tenorClient.getTestdata(!builder.isEmpty() ? builder.substring(5) : "");
    }

    private String convertString(String navn, String verdi) {

        return isBlank(verdi) ? "" : "+and+%s:%s".formatted(navn, verdi);
    }

    private String getRelasjonMedFoedselsdato(TenorRequest.Intervall relasjonMedFoedselsaar) {

        return isNull(relasjonMedFoedselsaar) ? "" : "+and+tenorRelasjoner.freg:{foedselsdato:[%s+to+%s]}"
                .formatted(
                        isNull(relasjonMedFoedselsaar.getFraOgMed()) ? "*" : relasjonMedFoedselsaar.getFraOgMed(),
                        isNull(relasjonMedFoedselsaar.getTilOgMed()) ? "*" : relasjonMedFoedselsaar.getTilOgMed());
    }

    private String getRelasjon(TenorRequest.Relasjon relasjon) {

        return isNull(relasjon) ? "" : "+and+tenorRelasjoner.freg:{tenorRelasjonsnavn:%s}".formatted(relasjon.name());
    }

    private String convertBoolean(String booleanNavn, Boolean booleanVerdi) {

        return isNull(booleanVerdi) ? "" : "+and+%s:%s".formatted(booleanNavn, booleanVerdi);
    }

    private String convertBooleanWildcard(String booleanNavn, Boolean booleanVerdi) {

        return isNotTrue(booleanVerdi) ? "" : "+and+%s:*".formatted(booleanNavn);
    }

    private String getIntervall(String intervallNavn, TenorRequest.Intervall intervall) {

        return isNull(intervall) ? "" : "+and+%s:[%s+to+%s]"
                .formatted(intervallNavn,
                        isNull(intervall.getFraOgMed()) ? "*" : intervall.getFraOgMed(),
                        isNull(intervall.getTilOgMed()) ? "*" : intervall.getTilOgMed());
    }

    private String getUtenlandskPersonidentifikasjon(List<TenorRequest.UtenlandskPersonIdentifikasjon> utenlandskPersonIdentifikasjon) {

        return (utenlandskPersonIdentifikasjon.isEmpty()) ? "" : "+data+utenlandskPersonidentifikasjon:(%s)"
                .formatted(utenlandskPersonIdentifikasjon.stream()
                        .map(Enum::name)
                        .collect(Collectors.joining("+and+")));
    }

    private String getRoller(List<TenorRequest.Roller> roller) {

        return (roller.isEmpty()) ? "" : "+and+tenorRelasjoner.brreg-er-fr:{dagligLeder:*}";
    }

    private String convertEnum(String enumNavn, Enum<?> enumVerdi) {

        return isNull(enumVerdi) ? "" : "+and+%s:%s%s".formatted(enumNavn,
                enumVerdi.name().substring(0, 1).toUpperCase(),
                enumVerdi.name().substring(1));
    }

    private String convertDatoer(String datoNavn, TenorRequest.DatoIntervall datoIntervall) {

        return isNull(datoIntervall) ? "" :
                "+and+%s:[%s+to+%s]".formatted(datoNavn, datoIntervall.getFra(), datoIntervall.getTil());
    }

    private String getIdentifikatorType(TenorRequest.IdentifikatorType identifikatorType) {

        if (isNull(identifikatorType)) {
            return "";
        }

        return "+and+identifikatorType:" + switch (identifikatorType) {
            case FNR -> "foedselsnummer";
            case DNR -> "dNummer";
            case FNR_TIDLIGERE_DNR -> "foedselsnummerOgTidligereDNummer";
        };
    }
}
