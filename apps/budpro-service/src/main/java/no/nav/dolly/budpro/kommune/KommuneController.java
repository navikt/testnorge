package no.nav.dolly.budpro.kommune;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/kommune")
@RequiredArgsConstructor
public class KommuneController {

    private final KommuneService service;

    @GetMapping("/all")
    List<Kommune> getAll() {
        return service.getAll();
    }

}
