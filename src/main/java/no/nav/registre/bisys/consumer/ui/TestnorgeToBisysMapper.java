package no.nav.registre.bisys.consumer.ui;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import no.nav.bidrag.ui.bisys.soknad.request.SoknadRequest;
import no.nav.registre.bisys.consumer.rs.request.BidragsmeldingAugments;
import no.nav.registre.bisys.consumer.rs.request.SynthesizedBidragRequest;
import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;

@Mapper
public interface TestnorgeToBisysMapper {

    @Mapping(target = "fnrBa", source = "syntetisertBidragsmelding.barnetsFnr")
    @Mapping(target = "fnrBm", source = "syntetisertBidragsmelding.bidragsmottaker")
    @Mapping(target = "fnrBp", source = "syntetisertBidragsmelding.bidragspliktig")
    SoknadRequest bidragsmeldingToSoknadRequest(SyntetisertBidragsmelding syntetisertBidragsmelding);

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
    SynthesizedBidragRequest bidragsmeldingToBidragRequest(BidragsmeldingAugments augments);

}
