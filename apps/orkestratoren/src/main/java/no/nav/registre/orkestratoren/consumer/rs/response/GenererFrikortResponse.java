package no.nav.registre.orkestratoren.consumer.rs.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenererFrikortResponse {

    private LeggPaaKoeStatus lagtPaaKoe;
    private String xml;

    public enum LeggPaaKoeStatus {
        OK,
        ERROR,
        NO
    }
}
