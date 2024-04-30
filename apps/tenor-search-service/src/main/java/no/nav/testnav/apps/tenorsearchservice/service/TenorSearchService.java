package no.nav.testnav.apps.tenorsearchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tenorsearchservice.consumers.TenorConsumer;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.InfoType;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorOversiktResponse;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorRequest;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorResponse;
import no.nav.testnav.apps.tenorsearchservice.mapper.TenorResultMapperService;
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
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.guard;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenorSearchService {

    private final TenorConsumer tenorConsumer;
    private final TenorResultMapperService tenorResultMapperService;
    private final PdlFilterService pdlFilterService;

    public Mono<TenorResponse> getTestdata(String testDataQuery, InfoType type, String fields, Integer seed) {

        return tenorConsumer.getTestdata(isNotBlank(testDataQuery) ? testDataQuery : "", type, fields, seed);
    }

    public Mono<TenorResponse> getTestdata(TenorRequest searchData, InfoType type, String fields,
                                           Integer antall, Integer side, Integer seed) {

        var query = getQuery(searchData);
        return tenorConsumer.getTestdata(query, type, fields, antall, side, seed);
    }

    private String getQuery(TenorRequest searchData) {

        var builder = new StringBuilder()
                .append(convertObject("identifikator", searchData.getIdentifikator()))
                .append(convertDatoer("foedselsdato", searchData.getFoedselsdato()))
                .append(convertDatoer("doedsdato", searchData.getDoedsdato()))
                .append(convertEnum("identifikatorType", searchData.getIdentifikatorType()))
                .append(convertEnum("kjoenn", searchData.getKjoenn()))
                .append(convertEnum("personstatus", searchData.getPersonstatus()))
                .append(convertEnum("sivilstand", searchData.getSivilstand()))
                .append(getUtenlandskPersonidentifikasjon(searchData.getUtenlandskPersonIdentifikasjon()))
                .append(convertEnum("identitetsgrunnlagStatus", searchData.getIdentitetsgrunnlagStatus()))
                .append(convertEnum("adresseBeskyttelse", searchData.getAdressebeskyttelse()))
                .append(convertBooleanWildcard("legitimasjonsdokument", searchData.getHarLegitimasjonsdokument()))
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
            builder.append(convertEnum("adresseGradering", searchData.getAdresser().getAdresseGradering()))
                    .append(convertObject("kommunenr", searchData.getAdresser().getKommunenummer()))
                    .append(convertBooleanWildcard("bostedsadresse", searchData.getAdresser().getHarBostedsadresse()))
                    .append(convertBooleanWildcard("oppholdAnnetSted", searchData.getAdresser().getHarOppholdAnnetSted()))
                    .append(convertBooleanWildcard("postadresse", searchData.getAdresser().getHarPostadresseNorge()))
                    .append(convertBooleanWildcard("postadresseUtland", searchData.getAdresser().getHarPostadresseUtland()))
                    .append(convertBooleanWildcard("kontaktinfoDoedsbo", searchData.getAdresser().getHarKontaktadresseDoedsbo()))
                    .append(convertObject("adresseSpesialtegn", searchData.getAdresser().getHarAdresseSpesialtegn()));
        }

        if (nonNull(searchData.getRelasjoner())) {
            builder.append(getFregRelasjoner(searchData.getRelasjoner()))
                    .append(convertIntervall("antallBarn", searchData.getRelasjoner().getAntallBarn()))
                    .append(convertBooleanWildcard("foreldreansvar", searchData.getRelasjoner().getHarForeldreAnsvar()))
                    .append(convertBooleanWildcard("deltBosted", searchData.getRelasjoner().getHarDeltBosted()))
                    .append(convertBooleanWildcard("vergemaalType", searchData.getRelasjoner().getHarVergemaalEllerFremtidsfullmakt()))
                    .append(convertObject("borMedFar", searchData.getRelasjoner().getBorMedFar()))
                    .append(convertObject("borMedMor", searchData.getRelasjoner().getBorMedMor()))
                    .append(convertObject("borMedMedmor", searchData.getRelasjoner().getBorMedMedmor()))
                    .append(convertObject("foreldreHarSammeAdresse", searchData.getRelasjoner().getForeldreHarSammeAdresse()));
        }

        if (nonNull(searchData.getHendelser())) {
            builder.append(convertEnum("hendelserMedSekvens.hendelse", searchData.getHendelser().getHendelse()))
                    .append(convertEnum("sisteHendelse", searchData.getHendelser().getSisteHendelse()));
        }

        builder.append(TenorEksterneRelasjonerUtility.getEksterneRelasjoner(searchData));

        return guard(builder);
    }

    private String getFregRelasjoner(TenorRequest.Relasjoner relasjoner) {

        return isNull(relasjoner.getRelasjon()) && isNull(relasjoner.getRelasjonMedFoedselsaar()) ? "" :
                " and tenorRelasjoner.freg:{%s}"
                        .formatted(guard(new StringBuilder()
                                .append(convertObject("tenorRelasjonsnavn", relasjoner.getRelasjon()))
                                .append(convertIntervall("foedselsdato", relasjoner.getRelasjonMedFoedselsaar()))
                        ));
    }

    private String getUtenlandskPersonidentifikasjon(List<TenorRequest.UtenlandskPersonIdentifikasjon> utenlandskPersonIdentifikasjon) {

        return (utenlandskPersonIdentifikasjon.isEmpty()) ? "" : " and utenlandskPersonidentifikasjon:(%s)"
                .formatted(utenlandskPersonIdentifikasjon.stream()
                        .map(Enum::name)
                        .collect(Collectors.joining(" and ")));
    }

    public Mono<TenorOversiktResponse> getTestdata(TenorRequest searchData, Integer antall,
                                                   Integer side, Integer seed, Boolean ikkeFiltrer) {

        var query = getQuery(searchData);

        return tenorConsumer.getTestdata(query, InfoType.IdentOgNavn, antall, side, seed)
                .flatMap(resultat -> Mono.just(tenorResultMapperService.map(resultat, query)))
                .flatMap(response -> isNotTrue(ikkeFiltrer) ?
                        pdlFilterService.filterPdlPerson(response) :
                        Mono.just(response));
    }
}
