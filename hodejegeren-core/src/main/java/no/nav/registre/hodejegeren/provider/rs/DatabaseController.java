package no.nav.registre.hodejegeren.provider.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.hodejegeren.mongodb.SyntHistorikk;
import no.nav.registre.hodejegeren.service.DatabaseService;

@RestController
@RequestMapping("api/v1/database")
public class DatabaseController {

    @Autowired
    private DatabaseService databaseService;

    @GetMapping(value = "hentAllHistorikk")
    public List<SyntHistorikk> hentAllHistorikk() {
        return databaseService.hentAllHistorikk();
    }

    @GetMapping(value = "hentHistorikkMedId/{id}")
    public SyntHistorikk hentHistorikkMedId(@PathVariable String id) {
        return databaseService.hentHistorikkMedId(id);
    }

    @PostMapping(value = "opprettHistorikk")
    public SyntHistorikk opprettHistorikk(@RequestBody SyntHistorikk syntHistorikk) {
        return databaseService.opprettHistorikk(syntHistorikk);
    }
}
