package no.nav.dolly.bestilling.instdata.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.domain.resultset.inst.InstdataInstitusjonstype;
import no.nav.dolly.domain.resultset.inst.InstdataKategori;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

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

                        instdata.setNorskident((String) context.getProperty("ident"));
                        instdata.setRegistrertAv("Dolly");

                        instdata.setOppholdstype(nullcheckSetDefaultValue(rsInstdata.getKategori(),
                                decideKategori(instdata.getInstitusjonstype())).name());
                        instdata.setTssEksternId(nullcheckSetDefaultValue(instdata.getTssEksternId(),
                                decideTssEksternId(instdata.getInstitusjonstype())));
                    }

                    private InstdataKategori decideKategori(InstdataInstitusjonstype type) {

                        return switch (type) {
                            case AS -> InstdataKategori.A;
                            case FO -> InstdataKategori.S;
                            default -> InstdataKategori.R;
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
