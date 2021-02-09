package no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Arbeidsforhold;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Opplysningspliktig;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.OpplysningspliktigList;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.EDAGM;

@Slf4j
@RequiredArgsConstructor
public class InntektsmottakerXmlArbeidsforholdRowMapper implements RowMapper<List<Arbeidsforhold>> {
    private final Integer total;

    @Override
    public List<Arbeidsforhold> mapRow(ResultSet rs, int rowNum) throws SQLException {
        if(rowNum % 10000 == 0){
            log.info("Antall rader behandlet {}/{}.", rowNum, total);
        }
        Opplysningspliktig opplysningspliktig = Opplysningspliktig.from(rs.getString("INNTEKTSMOTTAKER_XML"));
        return opplysningspliktig.toArbeidsforhold();
    }
}
