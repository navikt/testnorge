package no.nav.registre.aareg.consumer.rs.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MiljoerResponse {

    private List<String> miljoer;
}
