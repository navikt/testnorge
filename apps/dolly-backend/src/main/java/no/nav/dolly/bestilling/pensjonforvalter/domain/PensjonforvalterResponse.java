package no.nav.dolly.bestilling.pensjonforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PensjonforvalterResponse {

    private List<ResponseEnvironment> status;

    public List<ResponseEnvironment> getStatus() {

        if (isNull(status)) {
            status = new ArrayList<>();
        }
        return status;
    }

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
        private LocalDateTime timestamp;
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
