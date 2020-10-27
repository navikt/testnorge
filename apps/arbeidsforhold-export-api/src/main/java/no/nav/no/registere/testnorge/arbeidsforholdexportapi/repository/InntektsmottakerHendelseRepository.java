package no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InntektsmottakerHendelseRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<String> getXmlFrom(Set<String> identer) {
        String template = identer.stream().map(value -> "?").collect(Collectors.joining(","));
        return jdbcTemplate.queryForList(
                "SELECT INNTEKTSMOTTAKER_XML FROM T_INNTEKTSMOTTAKER_HENDELSE WHERE EFF_INNTEKTSMOTTAKER_ID IN (" + template + ")",
                identer.toArray(),
                String.class
        );
    }

}
