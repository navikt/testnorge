package no.nav.registre.inst;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Institusjonsopphold {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String personident;

    @JsonAlias({ "oppholdId", "funk_periode_id" })
    private Long oppholdId;

    @JsonAlias({ "tssEksternId", "tss_ekstern_id_fk" })
    private String tssEksternId;

    @JsonAlias({ "institusjonstype", "k_opphold_inst_t" })
    private String institusjonstype;

    @JsonAlias({ "varighet", "k_varig_inst_t" })
    private String varighet;

    @JsonAlias({ "kategori", "k_pas_ka_inst_t" })
    private String kategori;

    @JsonAlias({ "startdato", "dato_fom" })
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate startdato;

    @JsonAlias({ "faktiskSluttdato", "dato_tom" })
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate faktiskSluttdato;

    @JsonAlias({ "forventetSluttdato", "dato_tom_forventet" })
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate forventetSluttdato;

    @JsonAlias({ "kilde", "k_kilde_inst_t" })
    private String kilde;

    @JsonAlias({ "overfoert", "overfort" })
    private boolean overfoert;
}
