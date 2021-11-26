package no.nav.dolly.bestilling.instdata.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.domain.resultset.inst.InstdataInstitusjonstype;
import no.nav.dolly.domain.resultset.inst.InstdataKategori;
import no.nav.dolly.domain.resultset.inst.InstdataKilde;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.inst.TssEksternId.ADAMSTUEN_SYKEHJEM;
import static no.nav.dolly.domain.resultset.inst.TssEksternId.HELGELANDSSYKEHUSET_HF;
import static no.nav.dolly.domain.resultset.inst.TssEksternId.INDRE_OSTFOLD_FENGSEL;
import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;

@Component
public class InstdataMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsInstdata.class, Instdata.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInstdata rsInstdata, Instdata instdata, MappingContext context) {

                        if (nonNull(rsInstdata.getForventetSluttdato())) {
                            instdata.setSluttdato(rsInstdata.getForventetSluttdato().toLocalDate());
                        }
                        if (nonNull(rsInstdata.getFaktiskSluttdato())) {
                            instdata.setSluttdato(rsInstdata.getFaktiskSluttdato().toLocalDate());
                        }
                        instdata.setKategori(nullcheckSetDefaultValue(instdata.getKategori(), decideKategori(instdata.getInstitusjonstype())));
                        instdata.setKilde(nullcheckSetDefaultValue(instdata.getKilde(), decideKilde(instdata.getInstitusjonstype())));
                        instdata.setOverfoert(nullcheckSetDefaultValue(instdata.getOverfoert(), false));
                        instdata.setTssEksternId(nullcheckSetDefaultValue(instdata.getTssEksternId(), decideTssEksternId(instdata.getInstitusjonstype())));
                    }

                    private InstdataKategori decideKategori(InstdataInstitusjonstype type) {

                        return switch (type) {
                            case AS -> InstdataKategori.A;
                            case FO -> InstdataKategori.S;
                            default -> InstdataKategori.R;
                        };
                    }

                    private InstdataKilde decideKilde(InstdataInstitusjonstype type) {

                        return switch (type) {
                            case AS -> InstdataKilde.PP01;
                            case FO -> InstdataKilde.IT;
                            default -> InstdataKilde.INST;
                        };
                    }

                    private String decideTssEksternId(InstdataInstitusjonstype type) {

                        return switch (type) {
                            case AS -> ADAMSTUEN_SYKEHJEM.getId();
                            case FO -> INDRE_OSTFOLD_FENGSEL.getId();
                            default -> HELGELANDSSYKEHUSET_HF.getId();
                        };
                    }
                })
                .byDefault()
                .register();
    }
}
