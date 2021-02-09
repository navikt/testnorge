package no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Opplysningspliktig;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Permisjon;

@Slf4j
@RequiredArgsConstructor
public class InntektsmottakerXmlPermisjonerRowMapper implements RowMapper<List<Permisjon>> {
    private final Integer total;

    @Override
    public List<Permisjon> mapRow(ResultSet rs, int rowNum) throws SQLException {
        if ((rowNum + 1) % 10000 == 0 || (rowNum + 1) == total) {
            log.info("Antall rader behandlet {}/{}.", rowNum + 1, total);
        }
        Opplysningspliktig opplysningspliktig = Opplysningspliktig.from(rs.getString("INNTEKTSMOTTAKER_XML"));
        return opplysningspliktig.toPermisjoner();
    }
}
