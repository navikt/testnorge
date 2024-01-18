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
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertBooleanWildcard;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertDatoer;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertEnum;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertIntervall;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertObject;
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
                .append(convertObject("identifikator", searchData.getIdentifikator()))
                .append(convertDatoer("foedselsdato", searchData.getFoedselsdato()))
                .append(convertDatoer("doedsdato", searchData.getDoedsdato()))
                .append(getIdentifikatorType(searchData.getIdentifikatorType()))
                .append(convertEnum("kjoenn", searchData.getKjoenn()))
                .append(convertEnum("personstatus", searchData.getPersonstatus()))
                .append(convertEnum("sivilstatus", searchData.getSivilstatus()))
                .append(getUtenlandskPersonidentifikasjon(searchData.getUtenlandskPersonIdentifikasjon()))
                .append(convertEnum("identitetsgrunnlagStatus", searchData.getIdentitetsgrunnlagStatus()))
                .append(convertEnum("adressebeskyttelse", searchData.getAdressebeskyttelse()))
                .append(convertObject("legitimasjonsdokument", searchData.getHarLegitimasjonsdokument()))
                .append(convertObject("falskIdentitet", searchData.getHarFalskIdentitet()))
                .append(convertObject("norskStatsborgerskap", searchData.getHarNorskStatsborgerskap()))
                .append(convertObject("flereStatsborgerskap", searchData.getHarFlereStatsborgerskap()));

        if (nonNull(searchData.getNavn())) {
            builder.append(convertObject("flereFornavn", searchData.getNavn().getHarFlereFornavn()))
                    .append(convertIntervall("navnLengde", searchData.getNavn().getNavnLengde()))
                    .append(convertBooleanWildcard("mellomnavn", searchData.getNavn().getHarMellomnavn()))
                    .append(convertObject("navnSpesialtegn", searchData.getNavn().getHarNavnSpesialtegn()));
        }

        if (nonNull(searchData.getAdresser())) {
            builder.append(convertObject("bostedsadresse", searchData.getAdresser().getBostedsadresseFritekst()))
                    .append(convertBooleanWildcard("bostedsadresse", searchData.getAdresser().getHarBostedsadresse()))
                    .append(convertBooleanWildcard("oppholdAnnetSted", searchData.getAdresser().getHarOppholdAnnetSted()))
                    .append(convertBooleanWildcard("postadresse", searchData.getAdresser().getHarPostadresseNorge()))
                    .append(convertBooleanWildcard("postadresseUtland", searchData.getAdresser().getHarPostadresseUtland()))
                    .append(convertBooleanWildcard("kontaktinfoDoedsbo", searchData.getAdresser().getHarKontaktadresseDoedsbo()))
                    .append(convertObject("adresseSpesialtegn", searchData.getAdresser().getHarAdresseSpesialtegn()))
                    .append(convertEnum("bostedsadresseType", searchData.getAdresser().getBostedsadresseType()))
                    .append(convertEnum("coAdressenavnType", searchData.getAdresser().getCoAdresseType()));
        }

        if (nonNull(searchData.getRelasjoner())) {
            builder.append(getRelasjon(searchData.getRelasjoner().getRelasjon()))
                    .append(convertIntervall("antallBarn", searchData.getRelasjoner().getAntallBarn()))
                    .append(convertBooleanWildcard("foreldreansvar", searchData.getRelasjoner().getHarForeldreAnsvar()))
                    .append(getRelasjonMedFoedselsdato(searchData.getRelasjoner().getRelasjonMedFoedselsaar()))
                    .append(convertBooleanWildcard("deltBosted", searchData.getRelasjoner().getHarDeltBosted()))
                    .append(convertBooleanWildcard("vergemaalType", searchData.getRelasjoner().getHarVergemaalEllerFremtidsfullmakt()))
                    .append(convertObject("borMedFar", searchData.getRelasjoner().getBorMedFar()))
                    .append(convertObject("borMedMor", searchData.getRelasjoner().getBorMedMor()))
                    .append(convertObject("borMedMedMor", searchData.getRelasjoner().getBorMedMedmor()));
        }

        if (nonNull(searchData.getHendelser())) {
            builder.append(convertEnum("hendelserMedSekvens.hendelse", searchData.getHendelser().getHendelse()))
                    .append(convertEnum("sisteHendelse", searchData.getHendelser().getSisteHendelse()));
        }
        builder.append(TenorEksterneRelasjonerUtility.getEksterneRelasjoner(searchData));

        log.info("Søker med query: {}", builder.substring(5));
        return tenorClient.getTestdata(!builder.isEmpty() ? builder.substring(5) : "");
    }
    private String getRelasjonMedFoedselsdato(TenorRequest.Intervall relasjonMedFoedselsaar) {

        return isNull(relasjonMedFoedselsaar) ? "" : " and tenorRelasjoner.freg:{foedselsdato:[%sto%s]}"
                .formatted(
                        isNull(relasjonMedFoedselsaar.getFraOgMed()) ? "*" : relasjonMedFoedselsaar.getFraOgMed(),
                        isNull(relasjonMedFoedselsaar.getTilOgMed()) ? "*" : relasjonMedFoedselsaar.getTilOgMed());
    }
    private String getRelasjon(TenorRequest.Relasjon relasjon) {

        return isNull(relasjon) ? "" : " and tenorRelasjoner.freg:{tenorRelasjonsnavn:%s}".formatted(relasjon.name());
    }
    private String getUtenlandskPersonidentifikasjon(List<TenorRequest.UtenlandskPersonIdentifikasjon> utenlandskPersonIdentifikasjon) {

        return (utenlandskPersonIdentifikasjon.isEmpty()) ? "" : " and utenlandskPersonidentifikasjon:(%s)"
                .formatted(utenlandskPersonIdentifikasjon.stream()
                        .map(Enum::name)
                        .collect(Collectors.joining(" and ")));
    }
    private String getIdentifikatorType(TenorRequest.IdentifikatorType identifikatorType) {

        return isNull(identifikatorType) ? "" :
                " and identifikatorType:%s".formatted(switch (identifikatorType) {
                    case FNR -> "foedselsnummer";
                    case DNR -> "dNummer";
                    case FNR_TIDLIGERE_DNR -> "foedselsnummerOgTidligereDNummer";
                });
    }
}
