package no.nav.registre.testnorge.organisasjonmottak.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.testnorge.organisasjonmottak.consumer.OrganisasjonMottakConsumer;
import no.nav.registre.testnorge.organisasjonmottak.provider.dto.AnsatteDTO;
import no.nav.registre.testnorge.organisasjonmottak.provider.dto.DetaljertNavnDTO;
import no.nav.registre.testnorge.organisasjonmottak.provider.dto.KnytningDTO;
import no.nav.registre.testnorge.organisasjonmottak.provider.dto.NavnDTO;
import no.nav.registre.testnorge.organisasjonmottak.provider.dto.OrganisasjonDTO;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/organisasjonmottak")
public class OrganisasjonMottakController {
    private final OrganisasjonMottakConsumer organisasjonMottakConsumer;

    @PostMapping("/organisasjon")
    public void setorganisasjon(
            @RequestHeader("miljoe") String miljoe,
            @RequestBody OrganisasjonDTO dto
    ) {
        organisasjonMottakConsumer.send(dto.toRecord(miljoe));
    }

    @PutMapping("/navn")
    public void setNavn(
            @RequestHeader("miljoe") String miljoe,
            @RequestBody NavnDTO dto
    ) {
        organisasjonMottakConsumer.send(dto.toRecord(miljoe));
    }

    @PutMapping("/detaljert-navn")
    public void setDetaljertNavn(
            @RequestHeader("miljoe") String miljoe,
            @RequestBody DetaljertNavnDTO dto
    ) {
        organisasjonMottakConsumer.send(dto.toRecord(miljoe));
    }

    @PutMapping("/ansatte")
    public void setAnsatte(
            @RequestHeader("miljoe") String miljoe,
            @RequestBody AnsatteDTO dto
    ) {
        organisasjonMottakConsumer.send(dto.toRecord(miljoe));
    }

    @PutMapping("/knytning")
    public void setKnytning(
            @RequestHeader("miljoe") String miljoe,
            @RequestBody KnytningDTO dto
    ) {
        organisasjonMottakConsumer.send(dto.toRecord(miljoe));
    }
}
