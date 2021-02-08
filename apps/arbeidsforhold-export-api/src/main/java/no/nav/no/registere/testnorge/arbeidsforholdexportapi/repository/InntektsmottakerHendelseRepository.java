package no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.OpplysningspliktigList;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository.mapper.InntektsmottakerXmlRowMapper;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.EDAGM;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InntektsmottakerHendelseRepository {
    private final JdbcTemplate jdbcTemplate;

    public OpplysningspliktigList getAll(){
        var count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM AAREG_UTTREKK.temp_uttrekk_inhe",
                Integer.class
        );
        log.info("Henter {} INNTEKTSMOTTAKER_XML fra DB...", count);
        List<EDAGM> list = jdbcTemplate.query(
                "SELECT INNTEKTSMOTTAKER_XML FROM AAREG_UTTREKK.temp_uttrekk_inhe",
                new InntektsmottakerXmlRowMapper(count)
        );
        log.info("Hentet {} INNTEKTSMOTTAKER_XML fra DB.", list.size());
        return new OpplysningspliktigList(list);
    }

}
