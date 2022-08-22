package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.jpa.Testident.Master;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskAdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtflyttingDTO;

import java.util.List;
import java.util.concurrent.Callable;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
public class OriginatorCommand implements Callable<OriginatorCommand.Originator> {

    private final RsDollyUtvidetBestilling bestillingRequest;
    private final Testident testident;
    private final MapperFacade mapperFacade;

    private static String getSpesreg(AdressebeskyttelseDTO adressebeskyttelse) {

        return isNull(adressebeskyttelse.getGradering()) ? null :
                switch (adressebeskyttelse.getGradering()) {
                    case STRENGT_FORTROLIG, STRENGT_FORTROLIG_UTLAND -> "SPSF";
                    case FORTROLIG -> "SPFO";
                    default -> null;
                };
    }

    @Override
    public Originator call() {

        if (nonNull(testident) && testident.isPdlf() ||
                nonNull(bestillingRequest.getPdldata()) && nonNull((bestillingRequest.getPdldata().getOpprettNyPerson()))) {

            var context = new MappingContext.Factory().getContext();
            context.setProperty("navSyntetiskIdent", bestillingRequest.getNavSyntetiskIdent());

            return Originator.builder()
                    .pdlBestilling(mapperFacade.map(bestillingRequest.getPdldata(), BestillingRequestDTO.class, context))
                    .master(Master.PDLF)
                    .build();

        } else if (nonNull(testident) && testident.isPdl()) {

            return Originator.builder()
                    .master(Master.PDL)
                    .build();

        } else {

            var tpsfBestilling = nonNull(bestillingRequest.getTpsf()) ?
                    mapperFacade.map(bestillingRequest.getTpsf(), TpsfBestilling.class) : new TpsfBestilling();

            tpsfBestilling.setAntall(1);
            tpsfBestilling.setNavSyntetiskIdent(bestillingRequest.getNavSyntetiskIdent());
            tpsfBestilling.setHarIngenAdresse(nonNull(bestillingRequest.getPdldata()) &&
                    bestillingRequest.getPdldata().isPdlAdresse());

            if (nonNull(bestillingRequest.getPdldata()) && nonNull(bestillingRequest.getPdldata().getPerson())) {
                tpsfBestilling.setStatsborgerskap(
                        bestillingRequest.getPdldata().getPerson().getStatsborgerskap().stream().findFirst()
                                .orElse(new StatsborgerskapDTO()).getLandkode());
                tpsfBestilling.setStatsborgerskapRegdato(
                        bestillingRequest.getPdldata().getPerson().getStatsborgerskap().stream().findFirst()
                                .orElse(new StatsborgerskapDTO()).getGyldigFraOgMed());
                tpsfBestilling.setStatsborgerskapTildato(
                        bestillingRequest.getPdldata().getPerson().getStatsborgerskap().stream().findFirst()
                                .orElse(new StatsborgerskapDTO()).getGyldigTilOgMed());
                tpsfBestilling.setSpesreg(getSpesreg(bestillingRequest.getPdldata().getPerson().getAdressebeskyttelse()
                        .stream().findFirst().orElse(new AdressebeskyttelseDTO())));

                prepareUtflytting(tpsfBestilling);
                prepareInnflytting(tpsfBestilling);
            }

            return Originator.builder()
                    .tpsfBestilling(tpsfBestilling)
                    .master(Master.TPSF)
                    .build();
        }
    }

    private void prepareUtflytting(TpsfBestilling tpsfBestilling) {

        if (!bestillingRequest.getPdldata().getPerson().getUtflytting().isEmpty() &&
                bestillingRequest.getPdldata().getPerson().getUtflytting().stream()
                        .anyMatch(UtflyttingDTO::isVelkjentLand) &&
                !bestillingRequest.getPdldata().isPdlAdresse()) {
            tpsfBestilling.setHarIngenAdresse(true);
            bestillingRequest.getPdldata().getPerson().setKontaktadresse(List.of(
                    KontaktadresseDTO.builder()
                            .utenlandskAdresse(new UtenlandskAdresseDTO())
                            .build()));
        }
    }

    private void prepareInnflytting(TpsfBestilling tpsfBestilling) {

        if (!bestillingRequest.getPdldata().getPerson().getInnflytting().isEmpty() &&
                !bestillingRequest.getPdldata().isPdlAdresse()) {
            tpsfBestilling.setHarIngenAdresse(true);
            bestillingRequest.getPdldata().getPerson().setBostedsadresse(List.of(new BostedadresseDTO()));
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Originator {

        private BestillingRequestDTO pdlBestilling;
        private TpsfBestilling tpsfBestilling;
        private Master master;

        @JsonIgnore
        public boolean isTpsf() {
            return getMaster() == Master.TPSF;
        }

        @JsonIgnore
        public boolean isPdlf() {
            return getMaster() == Master.PDLF;
        }

        @JsonIgnore
        public boolean isPdl() {
            return getMaster() == Master.PDL;
        }
    }
}
