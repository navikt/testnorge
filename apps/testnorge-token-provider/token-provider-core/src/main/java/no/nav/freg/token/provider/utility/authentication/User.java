package no.nav.freg.token.provider.utility.authentication;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor class User {

    private String authId;
    private String template;
    private List<Callback> callbacks = new ArrayList<>();
    private String header;
    private String stage;

    @Data
    @NoArgsConstructor
    private static class Callback {

        private String type;
        private List<Name> input;
        private List<Name> output;
    }

    @Data
    @NoArgsConstructor
    private static class Name {

        private String name;
        private String value;
    }

    void setVerdi(
            String key,
            String value
    ) {
        if (value != null) {
            for (Callback callback : callbacks) {
                if (callback.getType() != null && callback.getType().startsWith(key) && callback.getInput() != null && callback.getInput().get(0) != null) {
                    callback.getInput().get(0).setValue(value);
                }
            }
        }
    }
}
