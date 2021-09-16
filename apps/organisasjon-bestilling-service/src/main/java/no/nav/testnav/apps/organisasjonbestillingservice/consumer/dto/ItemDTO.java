package no.nav.testnav.apps.organisasjonbestillingservice.consumer.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ItemDTO {
    ExecutableDTO executable;

    @JsonIgnore
    public Long getNumber() {
        return executable.getNumber();
    }
}