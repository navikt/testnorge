package no.nav.dolly.domain.resultset.sigrunstub;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KodeverknavnGrunnlag {
    private String tekniskNavn;
    @Field(enabled = false)
    private String verdi;
}