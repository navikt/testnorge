package no.nav.dolly.bestilling.arbeidsplassencv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CVDeleteDTO {

    private Boolean slettJobboensker;
    private List<String> arbeidserfaring;
    private List<String> utdanning;
    private List<String> andreGodkjenninger;
    private List<String> foererkort;
    private List<String> annenErfaring;
    private List<String> kurs;
    private List<String> spraak;
    private List<String> offentligeGodkjenninger;
    private List<String> fagbrev;
    private Boolean kompetanser;
    private Boolean sammendrag;
    private Boolean slettCv;
}
