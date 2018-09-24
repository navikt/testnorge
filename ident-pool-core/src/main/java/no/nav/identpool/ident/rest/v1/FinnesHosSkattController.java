package no.nav.identpool.ident.rest.v1;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import no.nav.identpool.ident.service.IdentpoolService;

@RestController
@RequestMapping("/api/v1/finneshosskatt")
@RequiredArgsConstructor
public class FinnesHosSkattController {

    private final IdentpoolService identpoolService;

    @PostMapping()
    public ApiResponse finnesHosSkatt(
            @RequestParam(value = "personidentifikator") String personidentifikator,
            @RequestParam(value = "foedselsdato") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate foedselsdato
    ) {
        FinnesHosSkattRequest finnesHosSkattRequest = FinnesHosSkattRequest.builder().personidentifikator(personidentifikator).foedselsdato(foedselsdato).build();
        identpoolService.registrerFinnesHosSkatt(finnesHosSkattRequest);
        return new ApiResponse("Registrert.");
    }
}
