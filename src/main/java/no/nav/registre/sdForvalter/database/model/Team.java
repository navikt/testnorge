package no.nav.registre.sdForvalter.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Entity
@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Team {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    @NotNull
    private String epost;

    @NotNull
    private String slackKanal;

    @NotNull
    private String navn;

    @JsonManagedReference(value = "tps")
    @OneToMany(mappedBy = "team")
    private Collection<TpsModel> tps;
    @JsonManagedReference(value = "aareg")
    @OneToMany(mappedBy = "team")
    private Collection<AaregModel> aareg;
    @JsonManagedReference(value = "krr")
    @OneToMany(mappedBy = "team")
    private Collection<KrrModel> krr;
    @JsonManagedReference(value = "ereg")
    @OneToMany(mappedBy = "team")
    private Collection<EregModel> ereg;

    @JsonManagedReference(value = "team-varigheter")
    @OneToMany(mappedBy = "team")
    private Collection<Varighet> varigheter;
}
