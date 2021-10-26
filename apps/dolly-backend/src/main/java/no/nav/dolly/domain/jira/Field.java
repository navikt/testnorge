package no.nav.dolly.domain.jira;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Field {

    private Boolean required;
    private Schema schema;
    private String name;
    private Boolean hasDefaultValue;
    private Set<String> operations;
    private List<AllowedValue> allowedValues;

    public List<AllowedValue> getAllowedValues() {
        if (allowedValues == null) {
            allowedValues = new ArrayList<>();
        }
        return allowedValues;
    }
}
