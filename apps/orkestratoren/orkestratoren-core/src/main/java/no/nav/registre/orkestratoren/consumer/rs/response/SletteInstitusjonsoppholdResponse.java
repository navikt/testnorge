package no.nav.registre.orkestratoren.consumer.rs.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SletteInstitusjonsoppholdResponse {

    private List<InstitusjonsoppholdResponse> instStatus;
}
