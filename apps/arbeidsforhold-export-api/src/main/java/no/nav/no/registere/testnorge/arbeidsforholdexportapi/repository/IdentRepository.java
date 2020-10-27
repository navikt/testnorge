package no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class IdentRepository {
    private final JdbcTemplate jdbcTemplate;

    private int count() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM T_IDENT", Integer.class);
        return count == null ? 0 : count;
    }

    public Set<String> getRandomIdenter(Integer antall){
        int min = Math.min(antall, count());
        log.info("Henter ut {} tilfeldige ident(er)", min);
        List<String> identer = jdbcTemplate.queryForList(
                "SELECT OFFENTLIG_IDENT FROM (SELECT * FROM T_IDENT ORDER BY DBMS_RANDOM.RANDOM) WHERE rownum <= ?",
                new Object[]{ min },
                String.class
        );
        return new HashSet<>(identer);
    }
}
