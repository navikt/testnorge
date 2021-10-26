package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static no.nav.dolly.domain.resultset.SystemTyper.AAREG;
import static no.nav.dolly.domain.resultset.SystemTyper.DOKARKIV;
import static no.nav.dolly.domain.resultset.SystemTyper.INNTKMELD;
import static no.nav.dolly.domain.resultset.SystemTyper.SYKEMELDING;
import static org.apache.http.util.TextUtils.isBlank;

@Service
@RequiredArgsConstructor
public class TransaksjonMappingService {

    private final TransaksjonMappingRepository transaksjonMappingRepository;
    private final BestillingProgressService bestillingProgressService;
    private final MapperFacade mapperFacade;

    public List<RsTransaksjonMapping> getTransaksjonMapping(String system, String ident, Long bestillingId) {

        final List<BestillingProgress> progress = bestillingProgressService.fetchBestillingProgressByBestillingId(bestillingId);

        if (isBlank(ident) && isNull(bestillingId)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Søket trenger enten Ident eller BestillingId");
        }
        List<TransaksjonMapping> transaksjonMappingList =
                transaksjonMappingRepository.findAllByBestillingIdAndIdent(bestillingId, ident).orElse(emptyList());
        List<RsTransaksjonMapping> rsTransaksjonMappings = mapperFacade.mapAsList(
                transaksjonMappingList.stream()
                        .filter(transaksjon -> isBlank(system) || transaksjon.getSystem().equals(system))
                        .collect(Collectors.toList()),
                RsTransaksjonMapping.class);
        rsTransaksjonMappings.forEach(transaksjon -> transaksjon.setStatus(!progress.isEmpty() ? hentSystemFeilFraBestillingProgress(progress, system) : null));
        return rsTransaksjonMappings;
    }

    public boolean existAlready(SystemTyper system, String ident, String miljoe) {

        return transaksjonMappingRepository.findAllBySystemAndIdent(system.name(), ident)
                .orElse(emptyList())
                .stream()
                .anyMatch(mapping -> miljoe.equals(mapping.getMiljoe()));
    }

    public void saveAll(Collection<TransaksjonMapping> entries) {

        entries.forEach(this::save);
    }

    @Transactional
    public void save(TransaksjonMapping entry) {

        transaksjonMappingRepository.save(entry);
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
        } else {
            status = progress.get(0).getFeil();
        }
        return isBlank(status) || status.contains("OK") ? "OK" : status;
    }
}
