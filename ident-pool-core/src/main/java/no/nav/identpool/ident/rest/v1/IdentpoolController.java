package no.nav.identpool.ident.rest.v1;

import static no.nav.identpool.util.PersonidentifikatorUtil.valider;

import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.ident.exception.UgyldigPersonidentifikatorException;
import no.nav.identpool.ident.repository.IdentEntity;
import no.nav.identpool.ident.service.IdentpoolService;

@Slf4j
@RestController
@Api(tags = { "identifikator" })
@RequestMapping("/api/v1/identifikator")
@RequiredArgsConstructor
public class IdentpoolController {

    private final IdentpoolService identpoolService;

    @PostMapping
    @ApiOperation(value = "${api.identifikator.rekvirer.description}")
    public List<String> rekvirer(@RequestBody @Valid HentIdenterRequest hentIdenterRequest) throws Exception {
        return identpoolService.finnIdenter(hentIdenterRequest);
    }

    @PostMapping("/bruk")
    @ApiOperation(value = "${api.identifikator.bruk.description}")
    public void markerBrukt(@RequestBody MarkerBruktRequest markerBruktRequest) throws Exception {
        valider(markerBruktRequest.getPersonidentifikator());
        identpoolService.markerBrukt(markerBruktRequest);
    }

    @GetMapping("/ledig")
    @ApiOperation(value = "${api.identifikator.ledig.description}")
    public Boolean erLedig(
            @RequestParam String personidentifikator
    ) throws UgyldigPersonidentifikatorException {
        valider(personidentifikator);
        return identpoolService.erLedig(personidentifikator);
    }

    @GetMapping
    @ApiOperation(value = "${api.identifikator.les.description}")
    public IdentEntity lesInnhold(@RequestParam(value = "personidentifikator") String personidentifikator
    ) throws UgyldigPersonidentifikatorException {
        valider(personidentifikator);
        return identpoolService.lesInnhold(personidentifikator);
    }
}
