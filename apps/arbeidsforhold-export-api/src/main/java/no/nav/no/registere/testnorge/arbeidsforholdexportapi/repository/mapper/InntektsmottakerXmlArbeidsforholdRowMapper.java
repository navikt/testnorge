package no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Arbeidsforhold;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Opplysningspliktig;

@Slf4j
@RequiredArgsConstructor
public class InntektsmottakerXmlArbeidsforholdRowMapper implements RowMapper<List<Arbeidsforhold>> {
    private final Integer page;
    private final Integer total;

    @Override
    public List<Arbeidsforhold> mapRow(ResultSet rs, int rowNum) throws SQLException {
        if ((rowNum + 1 + page) % 10000 == 0 || (rowNum + 1 + page) == total) {
            log.info("Antall rader behandlet {}/{}.", rowNum + 1 + page, total);
        }
        Opplysningspliktig opplysningspliktig = Opplysningspliktig.from(rs.getString("INNTEKTSMOTTAKER_XML"));
        return opplysningspliktig.toArbeidsforhold();
    }
}
