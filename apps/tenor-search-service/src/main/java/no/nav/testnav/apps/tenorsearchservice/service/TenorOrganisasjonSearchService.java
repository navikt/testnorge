package no.nav.testnav.apps.tenorsearchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tenorsearchservice.consumers.TenorOrganisasjonConsumer;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.InfoType;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorOrganisasjonRequest;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorOversiktOrganisasjonResponse;
import no.nav.testnav.apps.tenorsearchservice.service.mapper.TenorOrganisasjonResultMapperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static java.util.Objects.nonNull;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertBooleanWildcard;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertEnum;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertIntervall;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.convertObject;
import static no.nav.testnav.apps.tenorsearchservice.service.TenorConverterUtility.guard;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenorOrganisasjonSearchService {

    private final TenorOrganisasjonConsumer tenorClient;
    private final TenorOrganisasjonResultMapperService tenorOrganisasjonResultMapperService;

    public Mono<TenorOversiktOrganisasjonResponse> getTestdataOversiktOrganisasjon(TenorOrganisasjonRequest searchData, Integer antall, Integer side, Integer seed) {

        var query = getOrganisasjonQuery(searchData);

        return tenorClient.getOrganisasjonTestdata(query, InfoType.Organisasjon, antall, side, seed)
                .flatMap(resultat -> Mono.just(tenorOrganisasjonResultMapperService.mapOrganisasjon(resultat, query)));
    }

    public Mono<TenorOversiktOrganisasjonResponse> getTestdataOrganisasjon(TenorOrganisasjonRequest searchData) {

        var query = getOrganisasjonQuery(searchData);

        return tenorClient.getOrganisasjonTestdata(query, InfoType.AlleFelter, 1, 0, 0)
                .flatMap(resultat -> Mono.just(tenorOrganisasjonResultMapperService.mapOrganisasjon(resultat, query)));
    }

    private String getOrganisasjonQuery(TenorOrganisasjonRequest searchData) {

        var builder = new StringBuilder()
                .append(convertObject("organisasjonsnummer", searchData.getOrganisasjonsnummer()))
                .append(convertObject("harUtenlandskForretningsadresse", searchData.getHarUtenlandskForretningsadresse()))
                .append(convertObject("harUtenlandskPostadresse", searchData.getHarUtenlandskPostadresse()))
                .append(convertObject("naeringBeskrivelse", searchData.getNaeringBeskrivelse()))
                .append(convertObject("naeringKode", searchData.getNaeringKode()))
                .append(convertObject("registrertIMvaregisteret", searchData.getRegistrertIMvaregisteret()))
                .append(convertObject("registrertIForetaksregisteret", searchData.getRegistrertIForetaksregisteret()))
                .append(convertObject("registrertIFrivillighetsregisteret", searchData.getRegistrertIFrivillighetsregisteret()))
                .append(convertObject("slettetIEnhetsregisteret", searchData.getSlettetIEnhetsregisteret()))
                .append(convertIntervall("antallAnsatte", searchData.getAntallAnsatte()))

                .append(convertBooleanWildcard("revisorer", searchData.getRevisorer()))
                .append(convertBooleanWildcard("regnskapsfoerere", searchData.getRegnskapsfoerere()))
                .append(convertBooleanWildcard("dagligLeder", searchData.getDagligLeder()))
                .append(convertBooleanWildcard("styremedlemmer", searchData.getStyremedlemmer()))
                .append(convertBooleanWildcard("forretningsfoerer", searchData.getForretningsfoerer()))
                .append(convertBooleanWildcard("kontaktpersoner", searchData.getKontaktpersoner()))
                .append(convertBooleanWildcard("norsk_representant", searchData.getNorsk_representant()))

                .append(convertBooleanWildcard("harUnderenheter", searchData.getHarUnderenheter()))
                .append(convertObject("antallUnderenheter", searchData.getAntallUnderenheter()));


        if (nonNull(searchData.getOrganisasjonsform())) {
            builder.append(convertObject("organisasjonsform.kode", searchData.getOrganisasjonsform().getKode()));
        }

        if (nonNull(searchData.getForretningsadresse())) {
            builder.append(convertObject("forretningsadresse.kommunenummer", searchData.getForretningsadresse().getKommunenummer()));
        }

        if (nonNull(searchData.getEnhetStatuser())) {
            builder.append(convertObject("enhetStatuser.kode", searchData.getEnhetStatuser().getKode()));
        }

        if (nonNull(searchData.getErUnderenhet())) {
            builder.append(convertObject("erUnderenhet.overenhet", searchData.getErUnderenhet().getOverenhet()));
        }

        if (nonNull(searchData.getTenorRelasjoner())) {
            var tenorRelasjoner = searchData.getTenorRelasjoner();

            if (nonNull(tenorRelasjoner.getTestinnsendingSkattEnhet())) {
                var testinnsendingSkattEnhet = tenorRelasjoner.getTestinnsendingSkattEnhet();

                builder.append(convertObject("tenorRelasjoner.testinnsendingSkattEnhet.inntektsaar", testinnsendingSkattEnhet.getInntektsaar()));
                builder.append(convertBooleanWildcard("tenorRelasjoner.testinnsendingSkattEnhet.harSkattemeldingUtkast", testinnsendingSkattEnhet.getHarSkattemeldingUtkast()));
                builder.append(convertBooleanWildcard("tenorRelasjoner.testinnsendingSkattEnhet.harSkattemeldingFastsatt", testinnsendingSkattEnhet.getHarSkattemeldingFastsatt()));
                builder.append(convertBooleanWildcard("tenorRelasjoner.testinnsendingSkattEnhet.harSelskapsmeldingUtkast", testinnsendingSkattEnhet.getHarSelskapsmeldingUtkast()));
                builder.append(convertBooleanWildcard("tenorRelasjoner.testinnsendingSkattEnhet.harSelskapsmeldingFastsatt", testinnsendingSkattEnhet.getHarSelskapsmeldingFastsatt()));
                builder.append(convertEnum("tenorRelasjoner.testinnsendingSkattEnhet.manglendeGrunnlagsdata", testinnsendingSkattEnhet.getManglendeGrunnlagsdata()));
                builder.append(convertObject("tenorRelasjoner.testinnsendingSkattEnhet.manntall", testinnsendingSkattEnhet.getManntall()));
            }

            if (nonNull(tenorRelasjoner.getArbeidsforhold())) {
                var arbeidsforhold = tenorRelasjoner.getArbeidsforhold();

                builder.append(convertObject("tenorRelasjoner.arbeidsforhold.startDato", arbeidsforhold.getStartDato()));
                builder.append(convertObject("tenorRelasjoner.arbeidsforhold.sluttDato", arbeidsforhold.getSluttDato()));
                builder.append(convertBooleanWildcard("tenorRelasjoner.arbeidsforhold.harPermisjoner", arbeidsforhold.getHarPermisjoner()));
                builder.append(convertBooleanWildcard("tenorRelasjoner.arbeidsforhold.harPermitteringer", arbeidsforhold.getHarPermitteringer()));
                builder.append(convertBooleanWildcard("tenorRelasjoner.arbeidsforhold.harTimerMedTimeloenn", arbeidsforhold.getHarTimerMedTimeloenn()));
                builder.append(convertBooleanWildcard("tenorRelasjoner.arbeidsforhold.harUtenlandsopphold", arbeidsforhold.getHarUtenlandsopphold()));
                builder.append(convertBooleanWildcard("tenorRelasjoner.arbeidsforhold.harHistorikk", arbeidsforhold.getHarHistorikk()));
                builder.append(convertObject("tenorRelasjoner.arbeidsforhold.arbeidsforholdtype", arbeidsforhold.getArbeidsforholdtype()));
            }

            if (nonNull(tenorRelasjoner.getSamletReskontroinnsyn())) {
                var samletReskontroinnsyn = tenorRelasjoner.getSamletReskontroinnsyn();

                builder.append(convertObject("tenorRelasjoner.samletReskontroinnsyn.harKrav", samletReskontroinnsyn.getHarKrav()));
                builder.append(convertObject("tenorRelasjoner.samletReskontroinnsyn.harInnbetaling", samletReskontroinnsyn.getHarInnbetaling()));
            }

            if (nonNull(tenorRelasjoner.getTjenestepensjonsavtaleOpplysningspliktig())) {
                var tjenestepensjonsavtaleOpplysningspliktig = tenorRelasjoner.getTjenestepensjonsavtaleOpplysningspliktig();

                builder.append(convertObject("tenorRelasjoner.tjenestepensjonsavtaleOpplysningspliktig.tjenestepensjonsinnretningOrgnr", tjenestepensjonsavtaleOpplysningspliktig.getTjenestepensjonsinnretningOrgnr()));
                builder.append(convertObject("tenorRelasjoner.tjenestepensjonsavtaleOpplysningspliktig.periode", tjenestepensjonsavtaleOpplysningspliktig.getPeriode()));
            }

        }

        return guard(builder);
    }
}
