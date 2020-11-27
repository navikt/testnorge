package no.nav.registre.testnorge.organisasjonmottak.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public void setOrganiasjon(@RequestBody OrganiasjonDTO dto) {
        organiasjonMottakConsumer.send(dto.toRecord());
    }

    @PostMapping("/navn")
    public void setNavn(@RequestBody NavnDTO dto) {
        organiasjonMottakConsumer.send(dto.toRecord());
    }

    @PostMapping("/detaljert-navn")
    public void setNavn(@RequestBody DetaljertNavnDTO dto) {
        organiasjonMottakConsumer.send(dto.toRecord());
    }

    @PostMapping("/ansatte")
    public void setAnsatte(@RequestBody AnsatteDTO dto) {
        organiasjonMottakConsumer.send(dto.toRecord());
    }
}
