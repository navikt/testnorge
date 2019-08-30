package no.nav.registre.bisys.consumer.ui;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import no.nav.bidrag.ui.dto.SynthesizedBidragRequest;
import no.nav.registre.bisys.consumer.rs.request.BisysRequestAugments;
import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;

@Mapper
public interface TestnorgeToBisysMapper {

    @Mapping(target = "inntektBmEgneOpplysninger", source = "augments.inntektBmEgneOpplysninger")
    @Mapping(target = "inntektBpEgneOpplysninger", source = "augments.inntektBpEgneOpplysninger")
    @Mapping(target = "boforholdAndelForsorging", source = "augments.boforholdAndelForsorging")
    @Mapping(target = "boforholdBarnRegistrertPaaAdresse", source = "augments.boforholdBarnRegistrertPaaAdresse")
    @Mapping(target = "bidragsberegningKodeVirkAarsak", source = "augments.bidragsberegningKodeVirkAarsak")
    @Mapping(target = "bidragsberegningSamvarsklasse", source = "augments.bidragsberegningSamvarsklasse")
    @Mapping(target = "fatteVedtakGebyrBeslAarsakKode", source = "augments.fatteVedtakGebyrBeslAarsakKode")
    @Mapping(target = "fnrBa", source = "syntetisertBidragsmelding.barnetsFnr")
    @Mapping(target = "fnrBm", source = "syntetisertBidragsmelding.bidragsmottaker")
    @Mapping(target = "fnrBp", source = "syntetisertBidragsmelding.bidragspliktig")
    SynthesizedBidragRequest testnorgeToBisys(SyntetisertBidragsmelding syntetisertBidragsmelding,
            BisysRequestAugments augments);
}
