package no.nav.dolly.budpro.koststed;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/koststed")
public class KoststedController {

    private final KoststedService service;

    @GetMapping("/all")
    List<Koststed> getAll() {
        return service.getAll();
    }

}
