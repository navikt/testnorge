package no.nav.registre.frikort.provider.rs;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class SyntetiseringController {

    @Value("${test.string}")
    private String testString;

    @GetMapping(value = "/frikort")
    @ApiOperation(value = "Generer syntetiske frikort.")
    public String genererFrikort(){
        return testString;
    }

}
