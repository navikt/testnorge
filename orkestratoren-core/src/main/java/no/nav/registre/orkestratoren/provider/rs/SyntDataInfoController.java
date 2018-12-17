package no.nav.registre.orkestratoren.provider.rs;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import no.nav.registre.orkestratoren.service.SyntDataInfoService;

@RestController
@RequestMapping("api/v1/info")
public class SyntDataInfoController {

    @Autowired
    private SyntDataInfoService syntDataInfoService;
    @Value("${orkestratoren.batch.skdMeldingGruppeId}")
    private Long tpsBatchGruppeId;

    @ApiOperation(value = "Endepunktet returnerer samtlige FNR på levende nordmenn i "
            + "skdavspillergruppa i TPSF som orkestratorens tps-batch arbeider på. ",
            notes = "Se fasit-ressursen orkestratorProperties for gruppeId-en. ")
    @GetMapping("fnr/levende-nordmenn")
    public List<String> hentAlleLevendeNordmennFraTpsf() {
        return syntDataInfoService.opprettListenLevendeNordmenn(tpsBatchGruppeId);
    }
}
