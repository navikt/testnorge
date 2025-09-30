package no.nav.dolly.bestilling.pensjonforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PensjonforvalterResponse {

    @Builder.Default
    private List<ResponseEnvironment> status = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseEnvironment {

        private String miljo;
        private Response response;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private HttpStatus httpStatus;
        private String message;
        private String path;

        public boolean isResponse2xx() {
            return httpStatus.getStatus() >= 200 && httpStatus.getStatus() < 300;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HttpStatus {

        private String reasonPhrase;
        private Integer status;
    }
}
