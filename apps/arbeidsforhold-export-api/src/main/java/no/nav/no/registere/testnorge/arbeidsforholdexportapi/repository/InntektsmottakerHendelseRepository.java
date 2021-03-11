package no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Arbeidsforhold;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Inntekt;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Permisjon;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository.mapper.InntektsmottakerXmlArbeidsforholdRowMapper;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository.mapper.InntektsmottakerXmlInntekterRowMapper;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository.mapper.InntektsmottakerXmlPermisjonerRowMapper;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InntektsmottakerHendelseRepository {
    private final JdbcTemplate jdbcTemplate;

    public Integer count() {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM AAREG_UTTREKK.temp_uttrekk_inhe",
                Integer.class
        );
    }

    public List<Arbeidsforhold> getArbeidsforhold(int page, int size) {
        log.info("Henter {} INNTEKTSMOTTAKER_XML fra DB...", size);
        List<Arbeidsforhold> list = jdbcTemplate.query(
                "SELECT INNTEKTSMOTTAKER_XML FROM AAREG_UTTREKK.temp_uttrekk_inhe ORDER BY EFF_OPPLYSNINGSPLIKTIG_ID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
                new Object[]{ page * size, size},
                new InntektsmottakerXmlArbeidsforholdRowMapper(page * size, count())
        ).stream().flatMap(Collection::stream).collect(Collectors.toList());
        log.info("Hentet {} INNTEKTSMOTTAKER_XML med {} arbeidsforhold fra DB.", size, list.size());
        return list;
    }

    public List<Permisjon> getPermisjoner(int page, int size) {
        log.info("Henter {} INNTEKTSMOTTAKER_XML fra DB...", size);
        List<Permisjon> list = jdbcTemplate.query(
                "SELECT INNTEKTSMOTTAKER_XML FROM AAREG_UTTREKK.temp_uttrekk_inhe ORDER BY EFF_OPPLYSNINGSPLIKTIG_ID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
                new Object[]{ page * size, size},
                new InntektsmottakerXmlPermisjonerRowMapper(page * size, count())
        ).stream().flatMap(Collection::stream).collect(Collectors.toList());
        log.info("Hentet {} INNTEKTSMOTTAKER_XML med {} permisjoner fra DB.", size, list.size());
        return list;
    }

    public List<Inntekt> getInntekter(int page, int size) {
        log.info("Henter {} INNTEKTSMOTTAKER_XML fra DB...", size);
        List<Inntekt> list = jdbcTemplate.query(
                "SELECT INNTEKTSMOTTAKER_XML FROM AAREG_UTTREKK.temp_uttrekk_inhe ORDER BY EFF_OPPLYSNINGSPLIKTIG_ID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
                new Object[]{ page * size, size},
                new InntektsmottakerXmlInntekterRowMapper(page * size, count())
        ).stream().flatMap(Collection::stream).collect(Collectors.toList());
        log.info("Hentet {} INNTEKTSMOTTAKER_XML med {} inntekter fra DB.", size, list.size());
        return list;
    }
}
