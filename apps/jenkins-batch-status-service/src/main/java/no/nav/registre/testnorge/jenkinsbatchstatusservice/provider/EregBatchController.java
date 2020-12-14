package no.nav.registre.testnorge.jenkinsbatchstatusservice.provider;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ereg/batch/queue")
public class EregBatchController {

    @PostMapping("/items/{id}")
    public ResponseEntity<HttpStatus> register(@PathVariable("id") Long item) {
        return ResponseEntity
                .noContent()
                .build();
    }
}
