package no.nav.registre.bisys.consumer.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class PersonSearchRequest {
    private final String tag = "TESTNORGE";
    private final String excludeTag = "DOLLY";
    private final Boolean kunLevende = true;

    private Integer page;
    private Integer pageSize;
    private String randomSeed;
    private AlderSearch alder;

}
