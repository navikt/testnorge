package no.nav.identpool.consumers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TpsfStatusResponse {

    private List<StatusPaaIdent> statusPaaIdenter;

    public List<StatusPaaIdent> getStatusPaaIdenter() {
        if (Objects.isNull(statusPaaIdenter)) {
            statusPaaIdenter = new ArrayList<>();
        }
        return statusPaaIdenter;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusPaaIdent{

        private String ident;
        private List<String> env;
    }
}
