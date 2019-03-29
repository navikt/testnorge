package no.nav.registre.sdForvalter.database.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.List;

import no.nav.registre.sdForvalter.util.database.CreatableFromString;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class AaregModel extends AuditModel implements CreatableFromString {

    @Id
    private String fnr;

    @JsonProperty("orgId")
    private long orgId;

    @Override
    public void updateFromString(List<String> input, List<String> headers) {

        Class aaregModelClass = this.getClass();
        AaregModel model = this;

        for (int i = 0; i < headers.size(); i++) {
            try {
                Field field = aaregModelClass.getField(headers.get(i));
                field.set(model, input.get(i));
            } catch (NoSuchFieldException e) {
                log.warn("Mismatch between input data when creating from string for field: {}", headers.get(i));
                log.warn(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                log.warn(e.getMessage(), e);
            }
        }
    }
}
