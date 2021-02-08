package no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository.mapper;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.OpplysningspliktigList;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.EDAGM;

public class InntektsmottakerXmlRowMapper implements RowMapper<EDAGM> {
    @Override
    public EDAGM mapRow(ResultSet rs, int rowNum) throws SQLException {
        return OpplysningspliktigList.from(rs.getString("INNTEKTSMOTTAKER_XML"));
    }
}
