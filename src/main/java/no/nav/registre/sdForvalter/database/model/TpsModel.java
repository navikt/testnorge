package no.nav.registre.sdForvalter.database.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;

import no.nav.registre.sdForvalter.util.database.CreatableFromString;

@Entity
@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Table(name = "tps")
public class TpsModel extends AuditModel implements CreatableFromString {

    @Id
    @JsonProperty
    private String fnr;

    @JsonProperty("fornavn")
    private String firstName;
    @JsonProperty("etternavn")
    private String lastName;
    @JsonProperty("adresse")
    private String address;
    @JsonProperty("postnr")
    private String postNr;
    @JsonProperty("by")
    private String city;

    @JsonBackReference(value = "tps")
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @Override
    public void updateFromString(List<String> input, List<String> headers) {

        for (int i = 0; i < headers.size(); i++) {
            try {
                String fieldName = headers.get(i);
                if (input.size() != headers.size()) {
                    if (input.size() == i || input.size() == 0)
                        break;
                }
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
                    case "adresse":
                        this.setAddress(fieldValue);
                        break;
                    case "postnr":
                        this.setPostNr(fieldValue);
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
        if ("".equals(this.fnr) || this.fnr == null) {
            log.warn("Could not create fnr");
        }
    }
}
