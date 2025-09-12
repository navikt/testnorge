package no.nav.testnav.apps.tenorsearchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tenorsearchservice.consumers.TenorConsumer;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.DollyBackendSelector;
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
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertStringList;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertString;
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
                .append(convertObject("flereStatsborgerskap", searchData.getHarFlereStatsborgerskap()))
                .append(convertObject("nordenStatsborgerskap", searchData.getHarNordenStatsborgerskap()))
                .append(convertObject("euEoesStatsborgerskap", searchData.getHarEuEoesStatsborgerskap()))
                .append(convertObject("tredjelandStatsborgerskap", searchData.getHarTredjelandsStatsborgerskap()))
                .append(convertObject("utgaattStatsborgerskap", searchData.getHarUtgaattStatsborgerskap()))
                .append(convertObject("harStatsborgerskapHistorikk", searchData.getHarStatsborgerskapHistorikk()));

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

        if (nonNull(searchData.getAvansert())) {
            builder
                    .append(convertString("bostedsadresse", searchData.getAvansert().getBostedsadresse()))
                    .append(convertString("etternavn", searchData.getAvansert().getEtternavn()))
                    .append(convertObject("foedeland", searchData.getAvansert().getFoedeland()))
                    .append(convertString("fornavn", searchData.getAvansert().getFornavn()))
                    .append(convertString("mellomnavn", searchData.getAvansert().getMellomnavn()))
                    .append(convertStringList("statsborgerskap", searchData.getAvansert().getStatsborgerskap()))
                    .append(convertString("visningnavn", searchData.getAvansert().getVisningnavn()))
                    .append(convertObject("harBostedsadresseHistorikk", searchData.getAvansert().getHarBostedsadresseHistorikk()))
                    .append(convertObject("harDoedfoedtBarn", searchData.getAvansert().getHarDoedfoedtBarn()))
                    .append(convertObject("harForeldreMedSammeKjoenn", searchData.getAvansert().getHarForeldreMedSammeKjoenn()))
                    .append(convertObject("harInnflytting", searchData.getAvansert().getHarInnflytting()))
                    .append(convertObject("harNavnHistorikk", searchData.getAvansert().getHarNavnHistorikk()))
                    .append(convertObject("harOpphold", searchData.getAvansert().getHarOpphold()))
                    .append(convertObject("harPostadresseIFrittFormat", searchData.getAvansert().getHarPostadresseIFrittFormat()))
                    .append(convertObject("harPostadressePostboks", searchData.getAvansert().getHarPostadressePostboks()))
                    .append(convertObject("harPostadresseVegadresse", searchData.getAvansert().getHarPostadresseVegadresse()))
                    .append(convertObject("harPostboks", searchData.getAvansert().getHarPostboks()))
                    .append(convertObject("harRelatertPersonUtenFolkeregisteridentifikator", searchData.getAvansert().getHarRelatertPersonUtenFolkeregisteridentifikator()))
                    .append(convertObject("harRettsligHandleevne", searchData.getAvansert().getHarRettsligHandleevne()))
                    .append(convertObject("harSivilstandHistorikk", searchData.getAvansert().getHarSivilstandHistorikk()))
                    .append(convertObject("harUtenlandskAdresseIFrittFormat", searchData.getAvansert().getHarUtenlandskAdresseIFrittFormat()))
                    .append(convertObject("harUtenlandskPostadressePostboks", searchData.getAvansert().getHarUtenlandskPostadressePostboks()))
                    .append(convertObject("harUtenlandskPostadresseVegadresse", searchData.getAvansert().getHarUtenlandskPostadresseVegadresse()))
                    .append(convertObject("harUtflytting", searchData.getAvansert().getHarUtflytting()))
                    .append(convertString("barnFnr", searchData.getAvansert().getBarnFnr()))
                    .append(convertString("farFnr", searchData.getAvansert().getFarFnr()))
                    .append(convertString("morFnr", searchData.getAvansert().getMorFnr()))
                    .append(convertEnum("bostedsadresseType", searchData.getAvansert().getBostedsadresseType()))
                    .append(convertEnum("coAdressenavnType", searchData.getAvansert().getCoAdressenavnType()))
                    .append(convertEnum("vergeTjenestevirksomhet", searchData.getAvansert().getVergeTjenestevirksomhet()))
                    .append(convertEnum("vergemaalType", searchData.getAvansert().getVergemaalType()));
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
                                                   Integer side, Integer seed, DollyBackendSelector selector, Boolean ikkeFiltrer) {

        var query = getQuery(searchData);

        return tenorConsumer.getTestdata(query, InfoType.IdentOgNavn, antall, side, seed)
                .flatMap(resultat -> Mono.just(tenorResultMapperService.map(resultat, query)))
                .flatMap(response -> isNotTrue(ikkeFiltrer) ?
                        pdlFilterService.filterPdlPerson(response, selector) :
                        Mono.just(response));
    }
}
