package no.nav.skattekortservice.mapper;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.skattekortservice.dto.SkattekortResponsIntermediate;
import no.nav.testnav.libs.dto.skattekortservice.v1.Frikort;
import no.nav.testnav.libs.dto.skattekortservice.v1.Resultatstatus;
import no.nav.testnav.libs.dto.skattekortservice.v1.Skattekort;
import no.nav.testnav.libs.dto.skattekortservice.v1.Skattekortmelding;
import no.nav.testnav.libs.dto.skattekortservice.v1.Tabelltype;
import no.nav.testnav.libs.dto.skattekortservice.v1.Tilleggsopplysning;
import no.nav.testnav.libs.dto.skattekortservice.v1.Trekkode;
import no.nav.testnav.libs.dto.skattekortservice.v1.Trekkprosent;
import no.nav.testnav.libs.dto.skattekortservice.v1.Trekktabell;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Slf4j
@Component
public class ArbeidsgiverMappingStrategy implements MappingStrategy {
    @Override
    public void register(MapperFactory factory) {

        factory.classMap(SkattekortResponsIntermediate.Skattekortmelding.class, Skattekortmelding.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SkattekortResponsIntermediate.Skattekortmelding source, Skattekortmelding target, MappingContext context) {

                        target.setResultatPaaForespoersel(Resultatstatus.valueOf(source.getResultatPaaForespoersel().getValue()));
                        target.setArbeidstakeridentifikator(source.getArbeidstakeridentifikator());
                        target.setSkattekort(mapperFacade.map(source.getSkattekort(), Skattekort.class));
                        target.setTilleggsopplysning(source.getTilleggsopplysning().stream()
                                .map(tilleggsopplysning -> Tilleggsopplysning.valueOf(tilleggsopplysning.getValue()))
                                .toList());
                        target.setInntektsaar(source.getInntektsaar());
                    }
                })
                .register();

        factory.classMap(SkattekortResponsIntermediate.Frikort.class, Frikort.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SkattekortResponsIntermediate.Frikort source, Frikort target, MappingContext context) {

                        target.setTrekkode(getTrekkodeValue(source.getTrekkode()));
                        target.setFrikortbeloep(source.getFrikortbeloep());
                    }
                })
                .register();

        factory.classMap(SkattekortResponsIntermediate.Trekktabell.class, Trekktabell.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SkattekortResponsIntermediate.Trekktabell source, Trekktabell target, MappingContext context) {

                        target.setTrekkode(getTrekkodeValue(source.getTrekkode()));
                        target.setTabelltype(Tabelltype.valueOf(source.getTabelltype().getValue()));
                        target.setTabellnummer(source.getTabellnummer());
                        target.setProsentsats(source.getProsentsats());
                        target.setAntallMaanederForTrekk(source.getAntallMaanederForTrekk());
                    }
                })
                .register();

        factory.classMap(SkattekortResponsIntermediate.Trekkprosent.class, Trekkprosent.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SkattekortResponsIntermediate.Trekkprosent source, Trekkprosent target, MappingContext context) {

                        target.setTrekkode(getTrekkodeValue(source.getTrekkode()));
                        target.setProsentsats(source.getProsentsats());
                        target.setAntallMaanederForTrekk(source.getAntallMaanederForTrekk());
                    }
                })
                .register();
    }

    private static Trekkode getTrekkodeValue(SkattekortResponsIntermediate.Trekkode trekkode) {

        return nonNull(trekkode.getValue()) ? Trekkode.valueOf(trekkode.getValue()) : null;
    }
}
