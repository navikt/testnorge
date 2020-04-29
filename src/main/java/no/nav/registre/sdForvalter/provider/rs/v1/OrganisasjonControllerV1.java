package no.nav.registre.sdForvalter.provider.rs.v1;

import com.google.common.base.Strings;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import no.nav.registre.sdForvalter.adapter.EregAdapter;
import no.nav.registre.sdForvalter.consumer.rs.EregMapperConsumer;
import no.nav.registre.sdForvalter.domain.status.ereg.OrganisasjonStatusMap;
import no.nav.registre.sdForvalter.service.EregStatusService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/organisasjon")
public class OrganisasjonControllerV1 {

    private final EregMapperConsumer eregMapperConsumer;
    private final EregAdapter eregAdapter;
    private final EregStatusService eregStatusService;

    @GetMapping(value = "/flatfile", produces = "text/ISO-8859-1")
    public void exportEreg(
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
    public ResponseEntity<OrganisasjonStatusMap> getEregStatus(
            @RequestParam("miljo") String miljo,
            @RequestParam(value = "equal", required = false) Boolean equal,
            @RequestParam(value = "gruppe", required = false) String gruppe
    ) {
        return ResponseEntity.ok(eregStatusService.getStatus(miljo, gruppe, equal));
    }

}