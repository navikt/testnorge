package no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.OpplysningspliktigList;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.EDAGM;

@Slf4j
@RequiredArgsConstructor
public class InntektsmottakerXmlRowMapper implements RowMapper<EDAGM> {
    private final Integer total;

    @Override
    public EDAGM mapRow(ResultSet rs, int rowNum) throws SQLException {
        if(rowNum % 1000 == 0){
            log.info("Antall rader behandlet {}/{}.", rowNum, total);
        }
        return OpplysningspliktigList.from(rs.getString("INNTEKTSMOTTAKER_XML"));
    }
}
