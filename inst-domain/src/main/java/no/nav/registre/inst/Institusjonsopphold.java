package no.nav.registre.inst;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
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
    private LocalDate startdato;

    @JsonAlias({ "faktiskSluttdato", "dato_tom" })
    private LocalDate faktiskSluttdato;

    @JsonAlias({ "forventetSluttdato", "dato_tom_forventet" })
    private LocalDate forventetSluttdato;

    @JsonAlias({ "kilde", "k_kilde_inst_t" })
    private String kilde;

    @JsonAlias({ "overfoert", "overfort" })
    private boolean overfoert;

    //    public String getStartdato() {
    //        return parseDatoToInst2Format(startdato);
    //    }
    //
    //    public String getFaktiskSluttdato() {
    //        return parseDatoToInst2Format(faktiskSluttdato);
    //    }
    //
    //    public String getForventetSluttdato() {
    //        return parseDatoToInst2Format(forventetSluttdato);
    //    }
    //
    //    private String parseDatoToInst2Format(String date) {
    //        if (date == null) {
    //            return "";
    //        }
    //        if (date.length() == 10 && date.charAt(2) == '.' && date.charAt(5) == '.') {
    //            String day = date.substring(0, 2);
    //            String month = date.substring(3, 5);
    //            String year = date.substring(6);
    //            StringBuilder sb = new StringBuilder(year).append("-").append(month).append("-").append(day);
    //            return sb.toString();
    //        } else {
    //            return date;
    //        }
    //    }

    public void setStartdato(String startdato) {
        this.startdato = parseStringDateToLocalDate(startdato);
    }

    public void setStartdato(LocalDate startdato) {
        this.startdato = startdato;
    }

    public void setFaktiskSluttdato(String faktiskSluttdato) {
        this.faktiskSluttdato = parseStringDateToLocalDate(faktiskSluttdato);
    }

    public void setFaktiskSluttdato(LocalDate faktiskSluttdato) {
        this.faktiskSluttdato = faktiskSluttdato;
    }

    public void setForventetSluttdato(String forventetSluttdato) {
        this.forventetSluttdato = parseStringDateToLocalDate(forventetSluttdato);
    }

    public void setForventetSluttdato(LocalDate forventetSluttdato) {
        this.forventetSluttdato = forventetSluttdato;
    }

    private LocalDate parseStringDateToLocalDate(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        if (date.length() == 10 && date.charAt(2) == '.' && date.charAt(5) == '.') {
            int day = Integer.parseInt(date.substring(0, 2));
            int month = Integer.parseInt(date.substring(3, 5));
            int year = Integer.parseInt(date.substring(6));
            return LocalDate.of(year, month, day);
        } else if (date.length() == 10 && date.charAt(4) == '-' && date.charAt(7) == '-') {
            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(5, 7));
            int day = Integer.parseInt(date.substring(8));
            return LocalDate.of(year, month, day);
        } else {
            return null;
        }
    }
}
