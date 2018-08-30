package no.nav.identpool.ident.rs.v1;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/identer")
public class IdentpoolController {

    @GetMapping("/liste")
    public ResponseEntity<List<String>> get(@RequestParam Long antall) {
        return ResponseEntity.ok(null);
    }
}
