package no.nav.registre.bisys.consumer.ui;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import no.nav.bidrag.dto.SynthesizedBidragRequest;
import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;

@Mapper
public interface TestnorgeToBisysMapper {

  @Mapping(target = "fnrBa", source = "syntetisertBidragsmelding.barnetsFnr")
  @Mapping(target = "fnrBm", source = "syntetisertBidragsmelding.bidragsmottaker")
  @Mapping(target = "fnrBp", source = "syntetisertBidragsmelding.bidragspliktig")
  SynthesizedBidragRequest testnorgeToBisys(SyntetisertBidragsmelding syntetisertBidragsmelding);
}
