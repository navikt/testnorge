package no.nav.identpool.rs.v1;

import static no.nav.identpool.util.PersonidentifikatorUtil.validate;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import no.nav.identpool.exception.UgyldigPersonidentifikatorException;
import no.nav.identpool.service.IdentpoolService;

@RestController
@Api(tags = { "finnes hos skatt" })
@RequestMapping("/api/v1/finneshosskatt")
@RequiredArgsConstructor
public class FinnesHosSkattController {

    private final IdentpoolService identpoolService;

    @PostMapping
    @ApiOperation(value = "tjeneste som DREK bruker for å markere at DNR er i bruk og at det eksisterer hos SKD")
    @ApiImplicitParam(name = "Authorization", value = "\"Bearer\" + OIDC-token", required = true, dataType = "string", paramType = "header")
    public void finnesHosSkatt(
            @RequestBody IdentRequest identRequest
    ) throws UgyldigPersonidentifikatorException {
        validate(identRequest.getPersonidentifikator());
        identpoolService.registrerFinnesHosSkatt(identRequest.getPersonidentifikator());
    }
}
