package no.nav.registre.sdForvalter.provider.rs.v1;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Pattern;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import no.nav.registre.sdForvalter.adapter.EregAdapter;
import no.nav.registre.sdForvalter.consumer.rs.EregMapperConsumer;
import no.nav.registre.sdForvalter.domain.Ereg;
import no.nav.registre.sdForvalter.domain.EregListe;
import no.nav.registre.sdForvalter.domain.status.ereg.OrganisasjonStatusMap;
import no.nav.registre.sdForvalter.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.registre.sdForvalter.dto.organisasjon.v1.OrganisasjonListeDTO;
import no.nav.registre.sdForvalter.service.EregStatusService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/organisasjons")
public class OrganisasjonControllerV1 {

    private final EregMapperConsumer eregMapperConsumer;
    private final EregAdapter eregAdapter;
    private final EregStatusService eregStatusService;

    @GetMapping(value = "/flatfile", produces = "text/ISO-8859-1")
    public void export(
            @RequestParam(name = "gruppe") String gruppe,
            @RequestParam(name = "update") Boolean update,
            HttpServletResponse response
    ) throws IOException {
        response.setContentType("text/" + StandardCharsets.ISO_8859_1);
        response.setHeader("Content-Disposition", "attachment; filename=ereg-" + Strings.nullToEmpty(gruppe) + "-" + LocalDateTime.now().toString() + ".txt");
        String flatfil = eregMapperConsumer.generateFlatfil(eregAdapter.fetchBy(gruppe), update);
        response.getOutputStream().write(flatfil.getBytes(StandardCharsets.ISO_8859_1));
    }

    @GetMapping("/status")
    public ResponseEntity<OrganisasjonStatusMap> status(
            @RequestParam("miljo") String miljo,
            @RequestParam(value = "equal", required = false) Boolean equal,
            @RequestParam(value = "gruppe", required = false) String gruppe
    ) {
        return ResponseEntity.ok(eregStatusService.getStatus(miljo, gruppe, equal));
    }

    @GetMapping
    public ResponseEntity<OrganisasjonListeDTO> getOrganisasjons(@RequestParam(name = "gruppe", required = false) String gruppe) {
        OrganisasjonListeDTO dto = eregAdapter.fetchBy(gruppe).toDTO();
        return ResponseEntity.ok(dto);
    }

    @Validated
    @GetMapping("/{orgnr}")
    public ResponseEntity<OrganisasjonDTO> getOrganisasjon(@PathParam("orgnr") @Pattern(regexp = "^(8|9)\\d{8}$") String orgnr){
        Ereg ereg = eregAdapter.fetchByOrgnr(orgnr);

        return ResponseEntity.ok(ereg.toDTO());
    }

    @Validated
    @PutMapping
    public ResponseEntity<OrganisasjonListeDTO> createOrganisasjons(@RequestBody OrganisasjonListeDTO listeDTO) {
        OrganisasjonListeDTO dto = eregAdapter.save(new EregListe(listeDTO)).toDTO();
        return ResponseEntity.ok(dto);
    }

}