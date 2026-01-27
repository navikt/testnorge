package no.nav.skattekortservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.skattekortservice.dto.v2.ForskuddstrekkDTO;
import no.nav.skattekortservice.dto.v2.OpprettSkattekortRequest;
import no.nav.skattekortservice.dto.v2.ResultatForSkattekort;
import no.nav.skattekortservice.dto.v2.SkattekortDTO;
import no.nav.skattekortservice.dto.v2.TilleggsopplysningType;
import no.nav.skattekortservice.dto.v2.TrekkodeType;
import no.nav.testnav.libs.dto.skattekortservice.v1.Forskuddstrekk;
import no.nav.testnav.libs.dto.skattekortservice.v1.Frikort;
import no.nav.testnav.libs.dto.skattekortservice.v1.Resultatstatus;
import no.nav.testnav.libs.dto.skattekortservice.v1.Skattekortmelding;
import no.nav.testnav.libs.dto.skattekortservice.v1.Tilleggsopplysning;
import no.nav.testnav.libs.dto.skattekortservice.v1.Trekkode;
import no.nav.testnav.libs.dto.skattekortservice.v1.Trekkprosent;
import no.nav.testnav.libs.dto.skattekortservice.v1.Trekktabell;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
public class SkattekortRequestMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Skattekortmelding.class, OpprettSkattekortRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Skattekortmelding source, OpprettSkattekortRequest target, MappingContext context) {
                        target.setFnr(source.getArbeidstakeridentifikator());

                        var skattekortDTO = SkattekortDTO.builder()
                                .inntektsaar(source.getInntektsaar())
                                .resultatForSkattekort(mapResultatForSkattekort(source.getResultatPaaForespoersel()))
                                .tilleggsopplysningList(source.getTilleggsopplysning().stream()
                                        .map(SkattekortRequestMappingStrategy::mapTilleggsopplysning)
                                        .toList())
                                .build();

                        if (nonNull(source.getSkattekort())) {
                            skattekortDTO.setUtstedtDato(source.getSkattekort().getUtstedtDato());
                            skattekortDTO.setForskuddstrekkList(source.getSkattekort().getForskuddstrekk().stream()
                                    .map(SkattekortRequestMappingStrategy::mapForskuddstrekkDTO)
                                    .toList());
                        }

                        target.setSkattekort(skattekortDTO);
                    }
                })
                .register();
    }

    private static ForskuddstrekkDTO mapForskuddstrekkDTO(Forskuddstrekk source) {
        var target = new ForskuddstrekkDTO();
        if (nonNull(source.getFrikort())) {
            mapFrikort(source.getFrikort(), target);
        } else if (nonNull(source.getTrekktabell())) {
            mapTrekktabell(source.getTrekktabell(), target);
        } else if (nonNull(source.getTrekkprosent())) {
            mapTrekkprosent(source.getTrekkprosent(), target);
        }
        return target;
    }

    private static void mapFrikort(Frikort frikort, ForskuddstrekkDTO target) {
        target.setTrekkode(mapTrekkode(frikort.getTrekkode()));
        target.setFrikortBeloep(frikort.getFrikortbeloep());
    }

    private static void mapTrekktabell(Trekktabell trekktabell, ForskuddstrekkDTO target) {
        target.setTrekkode(mapTrekkode(trekktabell.getTrekkode()));
        target.setTabell(trekktabell.getTabellnummer());
        target.setProsentSats(nonNull(trekktabell.getProsentsats()) ? trekktabell.getProsentsats().doubleValue() : null);
        target.setAntallMndForTrekk(nonNull(trekktabell.getAntallMaanederForTrekk()) ? trekktabell.getAntallMaanederForTrekk().doubleValue() : null);
    }

    private static void mapTrekkprosent(Trekkprosent trekkprosent, ForskuddstrekkDTO target) {
        target.setTrekkode(mapTrekkode(trekkprosent.getTrekkode()));
        target.setProsentSats(nonNull(trekkprosent.getProsentsats()) ? trekkprosent.getProsentsats().doubleValue() : null);
        target.setAntallMndForTrekk(nonNull(trekkprosent.getAntallMaanederForTrekk()) ? trekkprosent.getAntallMaanederForTrekk().doubleValue() : null);
    }

    private static ResultatForSkattekort mapResultatForSkattekort(Resultatstatus resultatstatus) {
        if (isNull(resultatstatus)) {
            return ResultatForSkattekort.SKATTEKORTOPPLYSNINGER_OK;
        }
        return switch (resultatstatus) {
            case SKATTEKORTOPPLYSNINGER_OK -> ResultatForSkattekort.SKATTEKORTOPPLYSNINGER_OK;
            case IKKE_SKATTEKORT -> ResultatForSkattekort.IKKE_SKATTEKORT;
            case IKKE_TREKKPLIKT -> ResultatForSkattekort.IKKE_TREKKPLIKT;
            default -> ResultatForSkattekort.SKATTEKORTOPPLYSNINGER_OK;
        };
    }

    private static TrekkodeType mapTrekkode(Trekkode trekkode) {
        if (isNull(trekkode)) {
            return null;
        }
        return switch (trekkode) {
            case LOENN_FRA_HOVEDARBEIDSGIVER -> TrekkodeType.LOENN_FRA_HOVEDARBEIDSGIVER;
            case LOENN_FRA_BIARBEIDSGIVER -> TrekkodeType.LOENN_FRA_BIARBEIDSGIVER;
            case LOENN_FRA_NAV -> TrekkodeType.LOENN_FRA_NAV;
            case PENSJON -> TrekkodeType.PENSJON;
            case PENSJON_FRA_NAV -> TrekkodeType.PENSJON_FRA_NAV;
            case LOENN_TIL_UTENRIKSTJENESTEMANN -> TrekkodeType.LOENN_TIL_UTENRIKSTJENESTEMANN;
            case LOENN_KUN_TRYGDEAVGIFT_TIL_UTENLANDSK_BORGER ->
                    TrekkodeType.LOENN_KUN_TRYGDEAVGIFT_TIL_UTENLANDSK_BORGER;
            case LOENN_KUN_TRYGDEAVGIFT_TIL_UTENLANDSK_BORGER_SOM_GRENSEGJENGER ->
                    TrekkodeType.LOENN_KUN_TRYGDEAVGIFT_TIL_UTENLANDSK_BORGER_SOM_GRENSEGJENGER;
            case UFOERETRYGD_FRA_NAV -> TrekkodeType.UFOERETRYGD_FRA_NAV;
            case UFOEREYTELSER_FRA_ANDRE -> TrekkodeType.UFOEREYTELSER_FRA_ANDRE;
            case INTRODUKSJONSSTOENAD -> TrekkodeType.INTRODUKSJONSSTOENAD;
        };
    }

    private static TilleggsopplysningType mapTilleggsopplysning(Tilleggsopplysning tilleggsopplysning) {
        if (isNull(tilleggsopplysning)) {
            return null;
        }
        return switch (tilleggsopplysning) {
            case OPPHOLD_PAA_SVALBARD -> TilleggsopplysningType.OPPHOLD_PAA_SVALBARD;
            case KILDESKATTPENSJONIST -> TilleggsopplysningType.KILDESKATTPENSJONIST;
            case OPPHOLD_I_TILTAKSSONE -> TilleggsopplysningType.OPPHOLD_I_TILTAKSSONE;
            case KILDESKATT_PAA_LOENN -> TilleggsopplysningType.KILDESKATT_PAA_LOENN;
        };
    }
}
