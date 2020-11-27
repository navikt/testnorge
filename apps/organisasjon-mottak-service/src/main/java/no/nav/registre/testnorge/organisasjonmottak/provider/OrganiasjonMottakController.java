package no.nav.registre.testnorge.organisasjonmottak.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.HeaderParam;

import no.nav.registre.testnorge.organisasjonmottak.consumer.OrganiasjonMottakConsumer;
import no.nav.registre.testnorge.organisasjonmottak.provider.dto.AnsatteDTO;
import no.nav.registre.testnorge.organisasjonmottak.provider.dto.DetaljertNavnDTO;
import no.nav.registre.testnorge.organisasjonmottak.provider.dto.NavnDTO;
import no.nav.registre.testnorge.organisasjonmottak.provider.dto.OrganiasjonDTO;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/organiasjonmottak")
public class OrganiasjonMottakController {
    private final OrganiasjonMottakConsumer organiasjonMottakConsumer;


    @PostMapping("/organiasjon")
    public void setOrganiasjon(
            @HeaderParam("miljoe") String miljoe,
            @RequestBody OrganiasjonDTO dto
    ) {
        organiasjonMottakConsumer.send(dto.toRecord(miljoe));
    }

    @PostMapping("/navn")
    public void setNavn(
            @HeaderParam("miljoe") String miljoe,
            @RequestBody NavnDTO dto
    ) {
        organiasjonMottakConsumer.send(dto.toRecord(miljoe));
    }

    @PostMapping("/detaljert-navn")
    public void setNavn(
            @HeaderParam("miljoe") String miljoe,
            @RequestBody DetaljertNavnDTO dto
    ) {
        organiasjonMottakConsumer.send(dto.toRecord(miljoe));
    }

    @PostMapping("/ansatte")
    public void setAnsatte(
            @HeaderParam("miljoe") String miljoe,
            @RequestBody AnsatteDTO dto
    ) {
        organiasjonMottakConsumer.send(dto.toRecord(miljoe));
    }
}
