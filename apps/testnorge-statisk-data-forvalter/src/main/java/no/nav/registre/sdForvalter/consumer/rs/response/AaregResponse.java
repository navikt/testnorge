package no.nav.registre.sdForvalter.consumer.rs.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AaregResponse {

    private Map<String, String> statusPerMiljoe;
}
