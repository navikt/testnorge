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

import java.util.List;

import no.nav.registre.sdForvalter.util.database.CreatableFromString;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class TpsModel extends AuditModel implements CreatableFromString {

    @Id
    private String fnr;

    @JsonProperty("fornavn")
    private String firstName;
    @JsonProperty("etternavn")
    private String LastName;
    @JsonProperty("addresse")
    private String address;
    @JsonProperty("postnr")
    private int postNr;
    @JsonProperty("by")
    private String city;

    @Override
    public void updateFromString(List<String> input, List<String> headers) {

        for (int i = 0; i < headers.size(); i++) {
            try {
                String fieldName = headers.get(i);
                String fieldValue = input.get(i);

                switch (fieldName.toLowerCase()) {
                    case "fornavn":
                        this.setFirstName(fieldValue);
                        break;
                    case "fnr":
                        this.setFnr(fieldValue);
                        break;
                    case "etternavn":
                        this.setLastName(fieldValue);
                        break;
                    case "addresse":
                        this.setAddress(fieldValue);
                        break;
                    case "postnr":
                        this.setPostNr(Integer.getInteger(fieldValue));
                        break;
                    case "by":
                        this.setCity(fieldValue);
                        break;
                    default:
                        throw new NoSuchFieldException(fieldName);
                }
            } catch (NoSuchFieldException e) {
                log.warn("Mismatch between input data when creating from string for field: {}", headers.get(i));
                log.warn(e.getMessage(), e);
            }
        }
    }
}
