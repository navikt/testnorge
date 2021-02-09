package no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Arbeidsforhold;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.OpplysningspliktigList;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Permisjon;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository.mapper.InntektsmottakerXmlArbeidsforholdRowMapper;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository.mapper.InntektsmottakerXmlPermisjonerRowMapper;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.EDAGM;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InntektsmottakerHendelseRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<Arbeidsforhold> getAllArbeidsforhold(){
        var count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM AAREG_UTTREKK.temp_uttrekk_inhe",
                Integer.class
        );
        log.info("Henter {} INNTEKTSMOTTAKER_XML fra DB...", count);
        List<Arbeidsforhold> list = jdbcTemplate.query(
                "SELECT INNTEKTSMOTTAKER_XML FROM AAREG_UTTREKK.temp_uttrekk_inhe",
                new InntektsmottakerXmlArbeidsforholdRowMapper(count)
        ).stream().flatMap(Collection::stream).collect(Collectors.toList());
        log.info("Hentet {} INNTEKTSMOTTAKER_XML med {} arbeidsforhold fra DB.", count, list.size());
        return list;
    }


    public List<Permisjon> getAllPermisjoner(){
        var count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM AAREG_UTTREKK.temp_uttrekk_inhe",
                Integer.class
        );
        log.info("Henter {} INNTEKTSMOTTAKER_XML fra DB...", count);
        List<Permisjon> list = jdbcTemplate.query(
                "SELECT INNTEKTSMOTTAKER_XML FROM AAREG_UTTREKK.temp_uttrekk_inhe",
                new InntektsmottakerXmlPermisjonerRowMapper(count)
        ).stream().flatMap(Collection::stream).collect(Collectors.toList());
        log.info("Hentet {} INNTEKTSMOTTAKER_XML med {} arbeidsforhold fra DB.", count, list.size());
        return list;
    }
}
