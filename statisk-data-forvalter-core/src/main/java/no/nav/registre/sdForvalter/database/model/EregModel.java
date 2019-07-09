package no.nav.registre.sdForvalter.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
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
@Table(name = "EREG")
public class EregModel extends AuditModel implements CreatableFromString {

    @Id
    @GeneratedValue
    @JsonIgnore
    Long id;

    @NotNull
    @Column(unique = true)
    private String orgnr;

    @NotNull
    private String enhetstype;

    private String navn;
    private String epost;
    private String internetAdresse;

    private String naeringskode;

    private String parent;

    @Override
    public void updateFromString(List<String> input, List<String> headers) {

    }
}
