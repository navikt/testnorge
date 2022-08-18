package no.nav.testnav.apps.tpservice.consumer.rs.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PensjonforvalterResponse {

    private List<ResponseEnvironment> status;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseEnvironment {

        private String miljo;
        private Response response;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private HttpStatus httpStatus;
        private String message;
        private String path;
        private LocalDateTime timestamp;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HttpStatus {

        private String reasonPhrase;
        private Integer status;
    }
}
