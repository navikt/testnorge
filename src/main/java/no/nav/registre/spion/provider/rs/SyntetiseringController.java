package no.nav.registre.spion.provider.rs;

import java.util.List;
import java.util.Objects;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.spion.provider.rs.request.SyntetiserSpionRequest;
import no.nav.registre.spion.provider.rs.response.SyntetiserSpionResponse;
import no.nav.registre.spion.service.SyntetiseringService;

@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class SyntetiseringController {

    private final SyntetiseringService syntetiseringService;

    @Value("${avspillergruppe.id}")
    private String defaultAvspillergruppeId;

    @Value("${avspillergruppe.miljoe}")
    private String defaultMiljoe;

    @PostMapping(value = "/vedtak")
    @ApiOperation("Generer syntetiske vedtak for et gitt antall personer.")
    public List<SyntetiserSpionResponse> genererVedtak(@RequestBody SyntetiserSpionRequest request){

        return syntetiseringService.syntetiserVedtak(
                Objects.isNull(request.getAvspillergruppeId())?
                        Long.valueOf(defaultAvspillergruppeId) : request.getAvspillergruppeId() ,
                Objects.isNull(request.getMiljoe()) ? defaultMiljoe : request.getMiljoe(),
                request.getNumPersons(),
                request.getStartDate(),
                request.getEndDate(),
                request.getNumPeriods());
    }
}
