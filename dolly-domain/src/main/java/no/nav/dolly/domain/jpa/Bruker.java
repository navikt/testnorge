package no.nav.dolly.domain.jpa;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "T_BRUKER")
public class Bruker {

    public Bruker(String navIdent){
        this.navIdent = navIdent;
    }

    @Id
    @Column(name = "NAV_IDENT", length = 10)
    private String navIdent;

    @ManyToMany(mappedBy = "medlemmer")
    private Set<Team> teams = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "T_BRUKER_FAVORITTER",
            joinColumns = @JoinColumn(name = "bruker_id"),
            inverseJoinColumns = @JoinColumn(name = "testgruppe_id"))
    private Set<Testgruppe> favoritter = new HashSet<>();
}
