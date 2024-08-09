package no.nav.testnav.libs.dto.skattekortservice.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Arrays;
import java.util.Objects;

@Data
@SuperBuilder
@NoArgsConstructor
public class Forskuddstrekk {

    private Frikort frikort;
    private Trekktabell trekktabell;
    private Trekkprosent trekkprosent;

    @JsonIgnore
    public boolean isAllEmpty() {

        return contentsCount() == 0;
    }

    @JsonIgnore
    public boolean isAmbiguous() {

        return contentsCount() > 1;
    }

    private long contentsCount() {

        return Arrays.stream(getClass().getDeclaredFields())
                .map(field -> {
                    try {
                        field.setAccessible(true);
                        return field.get(this);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(Objects::nonNull)
                .count();
    }
}
