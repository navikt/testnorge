package no.nav.jpa;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(exclude = {"teamMedlemskap","teamEierskap"})
@ToString(exclude = {"teamMedlemskap","teamEierskap"})
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "T_BRUKER")
public class Bruker {

    @Id
    @Column(name = "NAV_IDENT", length = 10)
    private String navIdent;

    @ManyToMany(mappedBy = "brukere")
    @Column(name = "TEAM_MEDLEMSKAP")
    private Set<Team> teamMedlemskap;
    
    @OneToMany(mappedBy = "eier")
    @Column(name = "TEAM_EIERSKAP",unique = true)
    @JsonManagedReference
    private Set<Team> teamEierskap;
    
    public Bruker(String navIdent) {
        this.navIdent = navIdent;
    }
}
