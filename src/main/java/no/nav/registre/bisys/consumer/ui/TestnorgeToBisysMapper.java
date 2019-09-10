package no.nav.registre.bisys.consumer.ui;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import no.nav.bidrag.ui.dto.SynthesizedBidragRequest;
import no.nav.registre.bisys.consumer.rs.request.BisysRequestAugments;
import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;

@Mapper
public interface TestnorgeToBisysMapper {

    @Mapping(target = "andelForsorging", source = "augments.andelForsorging")
    @Mapping(target = "barnRegistrertPaaAdresse", source = "augments.barnRegistrertPaaAdresse")
    @Mapping(target = "gebyrBeslAarsakKode", source = "augments.gebyrBeslAarsakKode")
    @Mapping(target = "inntektBmEgneOpplysninger", source = "augments.inntektBmEgneOpplysninger")
    @Mapping(target = "inntektBpEgneOpplysninger", source = "augments.inntektBpEgneOpplysninger")
    @Mapping(target = "kodeUnntForsk", source = "augments.kodeUnntForsk")
    @Mapping(target = "samvarsklasse", source = "augments.samvarsklasse")
    @Mapping(target = "sartilskuddFradrag", source = "augments.sartilskuddFradrag")
    @Mapping(target = "sartilskuddGodkjentBelop", source = "augments.sartilskuddGodkjentBelop")
    @Mapping(target = "sartilskuddKravbelop", source = "augments.sartilskuddKravbelop")
    @Mapping(target = "sivilstandBm", source = "augments.sivilstandBm")
    @Mapping(target = "fnrBa", source = "syntetisertBidragsmelding.barnetsFnr")
    @Mapping(target = "fnrBm", source = "syntetisertBidragsmelding.bidragsmottaker")
    @Mapping(target = "fnrBp", source = "syntetisertBidragsmelding.bidragspliktig")
    SynthesizedBidragRequest testnorgeToBisys(SyntetisertBidragsmelding syntetisertBidragsmelding,
            BisysRequestAugments augments);
}
