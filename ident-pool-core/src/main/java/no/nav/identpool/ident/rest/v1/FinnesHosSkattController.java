package no.nav.identpool.ident.rest.v1;

import static no.nav.identpool.util.PersonidentifikatorUtil.valider;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import no.nav.identpool.ident.exception.UgyldigPersonidentifikatorException;
import no.nav.identpool.ident.service.IdentpoolService;

@RestController
@Api(tags = { "finnes hos skatt" })
@RequestMapping("/api/v1/finneshosskatt")
@RequiredArgsConstructor
public class FinnesHosSkattController {

    private final IdentpoolService identpoolService;

    @PostMapping
    @ApiOperation(value = "${finnes.hos.skatt.description}")
    @ApiImplicitParam(name = "Authorization", value = "\"Bearer\" + OIDC-token", required = true, dataType = "string", paramType = "header")
    public void finnesHosSkatt(
            @RequestBody String personidentifikator
    ) throws UgyldigPersonidentifikatorException {
        valider(personidentifikator);
        identpoolService.registrerFinnesHosSkatt(personidentifikator);
    }
}
