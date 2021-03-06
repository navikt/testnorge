package no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Arbeidsforhold;

@Slf4j
@RequiredArgsConstructor
public class InntektsmottakerXmlArbeidsforholdRowMapper implements RowMapper<List<? extends Arbeidsforhold>> {
    private final Integer page;
    private final Integer total;

    private boolean isV2_1(String xml) {
        return xml.contains("urn:nav:a-arbeidsforhold:v2_1");
    }

    @Override
    public List<? extends Arbeidsforhold> mapRow(ResultSet rs, int rowNum) throws SQLException {
        if ((rowNum + 1 + page) % 10000 == 0 || (rowNum + 1 + page) == total) {
            log.info("Antall rader behandlet {}/{}.", rowNum + 1 + page, total);
        }
        String xml = rs.getString("INNTEKTSMOTTAKER_XML");
        if (isV2_1(xml)) {
            var opplysningspliktig = no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.v2_1.Opplysningspliktig.from(xml);
            return opplysningspliktig.toArbeidsforhold();
        } else {
            var opplysningspliktig = no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.v2_0.Opplysningspliktig.from(xml);
            return opplysningspliktig.toArbeidsforhold();
        }

    }
}
