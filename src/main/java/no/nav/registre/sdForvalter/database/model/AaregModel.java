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

import no.nav.registre.sdForvalter.database.InTeam;
import no.nav.registre.sdForvalter.util.database.CreatableFromString;

@Entity
@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Table(name = "AAREG")
public class AaregModel extends AuditModel implements CreatableFromString, InTeam {

    @Id
    private String fnr;

    @JsonProperty("orgId")
    private long orgId;


    @ManyToOne
    @JsonBackReference(value = "aareg")
    @JoinColumn(name = "team_id")
    private Team team;

    @JsonBackReference(value = "aareg-varighet")
    @ManyToOne
    @JoinColumn(name = "varighet_id")
    private Varighet varighet;

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
                    case "fnr":
                        this.setFnr(fieldValue);
                        break;
                    case "orgid":
                        this.setOrgId(Long.valueOf(fieldValue.replaceAll("\\s+", "")));
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
