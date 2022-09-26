package no.nav.dolly.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
public class DeleteZIdentResponse {

    private String bruker;
    private List<Gruppe> grupper;

    public List<Gruppe> getGrupper() {
        if (isNull(grupper)) {
            grupper = new ArrayList<>();
        }
        return grupper;
    }

    @Data
    @Builder
    public static class Gruppe {

        private Long id;
        private List<String> identer;

        public List<String> getIdenter() {

            if (isNull(identer)) {
                identer = new ArrayList<>();
            }
            return identer;
        }
    }
}
