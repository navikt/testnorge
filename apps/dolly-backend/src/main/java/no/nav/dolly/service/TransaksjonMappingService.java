package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static no.nav.dolly.domain.resultset.SystemTyper.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class TransaksjonMappingService {

    private final TransaksjonMappingRepository transaksjonMappingRepository;
    private final MapperFacade mapperFacade;

    @Transactional(readOnly = true)
    public List<RsTransaksjonMapping> getTransaksjonMapping(String system, String ident, Long bestillingId) {

        return transaksjonMappingRepository.findAllByBestillingIdAndIdent(bestillingId, ident).stream()
                .filter(transaksjon -> isNull(system) || system.equals(transaksjon.getSystem()))
                .map(transasjon -> mapperFacade.map(transasjon, RsTransaksjonMapping.class))
                .toList();
    }

    public boolean existAlready(SystemTyper system, String ident, String miljoe) {

        return transaksjonMappingRepository.findAllBySystemAndIdent(system.name(), ident)
                .orElse(emptyList())
                .stream()
                .anyMatch(mapping -> isNull(miljoe) || miljoe.equals(mapping.getMiljoe()));
    }

    public void saveAll(Collection<TransaksjonMapping> entries) {

        entries.forEach(this::save);
    }

    @Transactional
    public void save(TransaksjonMapping entry) {

        transaksjonMappingRepository.save(entry);
    }

    @Transactional
    public void slettTransaksjonMappingByTestident(String ident) {

        transaksjonMappingRepository.deleteAllByIdent(ident);
    }

    private String hentSystemFeilFraBestillingProgress(List<BestillingProgress> progress, String system) {

        String status = "";
        if (SYKEMELDING.name().equals(system)) {
            status = progress.get(0).getSykemeldingStatus();
        } else if (DOKARKIV.name().equals(system)) {
            status = progress.get(0).getDokarkivStatus();
        } else if (INNTKMELD.name().equals(system)) {
            status = progress.get(0).getInntektsmeldingStatus();
        } else if (AAREG.name().equals(system)) {
            status = progress.get(0).getAaregStatus();
        } else if (PEN_AP.name().equals(system)) {
            status = progress.get(0).getPensjonforvalterStatus();
        } else {
            status = progress.get(0).getFeil();
        }
        return isBlank(status) || status.contains("OK") ? "OK" : status;
    }
}
