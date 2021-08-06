package no.nav.registre.inst;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class InstitusjonsoppholdV2 {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String norskident;

    @JsonAlias({ "tssEksternId", "tss_ekstern_id_fk" })
    private String tssEksternId;

    @JsonAlias({ "institusjonstype", "k_opphold_inst_t" })
    private String institusjonstype;

    @JsonAlias({ "kategori", "k_pas_ka_inst_t" })
    private String oppholdstype;

    @JsonAlias({ "startdato", "dato_fom" })
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate startdato;

    @JsonAlias({ "faktiskSluttdato", "dato_tom" })
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate sluttdato;

    @JsonIgnore
    private String registrertAv;
}
