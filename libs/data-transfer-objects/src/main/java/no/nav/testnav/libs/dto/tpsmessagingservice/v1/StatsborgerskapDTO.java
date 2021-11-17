package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StatsborgerskapDTO {

    @JsonIgnore
    private PersonDTO person;

    private String statsborgerskap;

    private LocalDateTime statsborgerskapRegdato;

    private LocalDateTime statsborgerskapTildato;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof StatsborgerskapDTO)) {
            return false;
        }

        StatsborgerskapDTO that = (StatsborgerskapDTO) o;

        return new EqualsBuilder()
                .append(getStatsborgerskap(), that.getStatsborgerskap())
                .append(getStatsborgerskapRegdato(), that.getStatsborgerskapRegdato())
                .append(getStatsborgerskapTildato(), that.getStatsborgerskapTildato())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getStatsborgerskap())
                .append(getStatsborgerskapRegdato())
                .append(getStatsborgerskapTildato())
                .toHashCode();
    }
}