package no.nav.dolly.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Month;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOversiktDTO {

    @JsonIgnore
    private String aarManed;

    private Integer aar;
    private Month maaned;
    private Integer totaltAntallPersoner;
    private Integer nye;
    private Integer gjenopprettede;
}
