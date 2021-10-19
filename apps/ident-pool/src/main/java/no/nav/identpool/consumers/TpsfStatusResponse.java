package no.nav.identpool.consumers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TpsfStatusResponse {

    private List<StatusPaaIdent> statusPaaIdenter;

    public List<StatusPaaIdent> getStatusPaaIdenter() {
        if (isNull(statusPaaIdenter)) {
            statusPaaIdenter = new ArrayList<>();
        }
        return statusPaaIdenter;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusPaaIdent{

        private String ident;
        private List<String> env;

        public List<String> getEnv() {

            if (isNull(env)) {
                env = new ArrayList<>();
            }
            return env;
        }
    }
}
