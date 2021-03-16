package no.nav.tpsidenter.vedlikehold.provider;

import lombok.RequiredArgsConstructor;
import no.nav.tpsidenter.vedlikehold.service.TpsfService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/testdata")
@RequiredArgsConstructor
public class DeleteController {

    private final TpsfService tpsfService;


    @DeleteMapping
    public List<String> startDelete() {

        return tpsfService.deleteIdents();
    }
}
