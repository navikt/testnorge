package no.nav.skattekortservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.skattekortservice.dto.v2.ForskuddstrekkDTO;
import no.nav.skattekortservice.dto.v2.ResultatForSkattekort;
import no.nav.skattekortservice.dto.v2.SkattekortDTO;
import no.nav.skattekortservice.dto.v2.TilleggsopplysningType;
import no.nav.skattekortservice.dto.v2.TrekkodeType;
import no.nav.testnav.libs.dto.skattekortservice.v1.Forskuddstrekk;
import no.nav.testnav.libs.dto.skattekortservice.v1.Frikort;
import no.nav.testnav.libs.dto.skattekortservice.v1.Resultatstatus;
import no.nav.testnav.libs.dto.skattekortservice.v1.Skattekort;
import no.nav.testnav.libs.dto.skattekortservice.v1.Skattekortmelding;
import no.nav.testnav.libs.dto.skattekortservice.v1.Tilleggsopplysning;
import no.nav.testnav.libs.dto.skattekortservice.v1.Trekkode;
import no.nav.testnav.libs.dto.skattekortservice.v1.Trekkprosent;
import no.nav.testnav.libs.dto.skattekortservice.v1.Trekktabell;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
public class SkattekortResponseMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(SkattekortDTO.class, Skattekortmelding.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SkattekortDTO source, Skattekortmelding target, MappingContext context) {
                        target.setInntektsaar(source.getInntektsaar());
                        target.setResultatPaaForespoersel(mapResultatstatus(source.getResultatForSkattekort()));
                        target.setTilleggsopplysning(mapTilleggsopplysningList(source.getTilleggsopplysningList()));

                        var skattekort = Skattekort.builder()
                                .utstedtDato(source.getUtstedtDato())
                                .forskuddstrekk(mapForskuddstrekkList(source.getForskuddstrekkList()))
                                .build();
                        target.setSkattekort(skattekort);
                    }
                })
                .register();
    }

    private static Resultatstatus mapResultatstatus(ResultatForSkattekort resultat) {
        if (isNull(resultat)) {
            return null;
        }
        return switch (resultat) {
            case SKATTEKORTOPPLYSNINGER_OK -> Resultatstatus.SKATTEKORTOPPLYSNINGER_OK;
            case IKKE_SKATTEKORT -> Resultatstatus.IKKE_SKATTEKORT;
            case IKKE_TREKKPLIKT -> Resultatstatus.IKKE_TREKKPLIKT;
        };
    }

    private static List<Tilleggsopplysning> mapTilleggsopplysningList(List<TilleggsopplysningType> tilleggsopplysninger) {
        if (isNull(tilleggsopplysninger)) {
            return List.of();
        }
        return tilleggsopplysninger.stream()
                .map(SkattekortResponseMappingStrategy::mapTilleggsopplysning)
                .toList();
    }

    private static Tilleggsopplysning mapTilleggsopplysning(TilleggsopplysningType type) {
        if (isNull(type)) {
            return null;
        }
        return switch (type) {
            case OPPHOLD_PAA_SVALBARD -> Tilleggsopplysning.OPPHOLD_PAA_SVALBARD;
            case KILDESKATTPENSJONIST -> Tilleggsopplysning.KILDESKATTPENSJONIST;
            case OPPHOLD_I_TILTAKSSONE -> Tilleggsopplysning.OPPHOLD_I_TILTAKSSONE;
            case KILDESKATT_PAA_LOENN -> Tilleggsopplysning.KILDESKATT_PAA_LOENN;
        };
    }

    private static List<Forskuddstrekk> mapForskuddstrekkList(List<ForskuddstrekkDTO> forskuddstrekkList) {
        if (isNull(forskuddstrekkList)) {
            return List.of();
        }
        return forskuddstrekkList.stream()
                .map(SkattekortResponseMappingStrategy::mapForskuddstrekk)
                .toList();
    }

    private static Forskuddstrekk mapForskuddstrekk(ForskuddstrekkDTO dto) {
        if (nonNull(dto.getFrikortBeloep())) {
            return Forskuddstrekk.builder()
                    .frikort(Frikort.builder()
                            .trekkode(mapTrekkode(dto.getTrekkode()))
                            .frikortbeloep(dto.getFrikortBeloep())
                            .build())
                    .build();
        } else if (nonNull(dto.getTabell())) {
            return Forskuddstrekk.builder()
                    .trekktabell(Trekktabell.builder()
                            .trekkode(mapTrekkode(dto.getTrekkode()))
                            .tabellnummer(dto.getTabell())
                            .prosentsats(nonNull(dto.getProsentSats()) ? dto.getProsentSats().intValue() : null)
                            .antallMaanederForTrekk(nonNull(dto.getAntallMndForTrekk()) ? dto.getAntallMndForTrekk().intValue() : null)
                            .build())
                    .build();
        } else {
            return Forskuddstrekk.builder()
                    .trekkprosent(Trekkprosent.builder()
                            .trekkode(mapTrekkode(dto.getTrekkode()))
                            .prosentsats(nonNull(dto.getProsentSats()) ? dto.getProsentSats().intValue() : null)
                            .antallMaanederForTrekk(nonNull(dto.getAntallMndForTrekk()) ? dto.getAntallMndForTrekk().intValue() : null)
                            .build())
                    .build();
        }
    }

    private static Trekkode mapTrekkode(TrekkodeType type) {
        if (isNull(type)) {
            return null;
        }
        return switch (type) {
            case LOENN_FRA_HOVEDARBEIDSGIVER -> Trekkode.LOENN_FRA_HOVEDARBEIDSGIVER;
            case LOENN_FRA_BIARBEIDSGIVER -> Trekkode.LOENN_FRA_BIARBEIDSGIVER;
            case LOENN_FRA_NAV -> Trekkode.LOENN_FRA_NAV;
            case PENSJON -> Trekkode.PENSJON;
            case PENSJON_FRA_NAV -> Trekkode.PENSJON_FRA_NAV;
            case LOENN_TIL_UTENRIKSTJENESTEMANN -> Trekkode.LOENN_TIL_UTENRIKSTJENESTEMANN;
            case LOENN_KUN_TRYGDEAVGIFT_TIL_UTENLANDSK_BORGER -> Trekkode.LOENN_KUN_TRYGDEAVGIFT_TIL_UTENLANDSK_BORGER;
            case LOENN_KUN_TRYGDEAVGIFT_TIL_UTENLANDSK_BORGER_SOM_GRENSEGJENGER -> Trekkode.LOENN_KUN_TRYGDEAVGIFT_TIL_UTENLANDSK_BORGER_SOM_GRENSEGJENGER;
            case UFOERETRYGD_FRA_NAV -> Trekkode.UFOERETRYGD_FRA_NAV;
            case UFOEREYTELSER_FRA_ANDRE -> Trekkode.UFOEREYTELSER_FRA_ANDRE;
            case INTRODUKSJONSSTOENAD -> Trekkode.INTRODUKSJONSSTOENAD;
        };
    }
}
