package no.nav.registre.testnorge.identservice.testdata.servicerutiner.authorization;

import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * System user roles
 */
public enum UserRole implements GrantedAuthority {

    ROLE_ACCESS("0000-GA-TPSF-LES", "0000-GA-TPSF-SKRIV", "0000-GA-TPSF-SKDMELDING","0000-GA-TPSF-SERVICERUTINER"),
    ROLE_TPSF_SKRIV("0000-GA-TPSF-SKRIV"),
    ROLE_TPSF_LES("0000-GA-TPSF-LES"),
    ROLE_TPSF_SKDMELDING("0000-GA-TPSF-SKDMELDING"),
    ROLE_TPSF_SERVICERUTINER("0000-GA-TPSF-SERVICERUTINER"),
    ROLE_TPSF_AVSPILLER("0000-GA-TPSF-AVSPILLER"),
    ROLE_TPSF_AVSPILLER_TEAM_REG("0000-GA-TPSF-AVSPILLER-TEAM-REG"),
    ROLE_DISKRESJONESKODE_6_READ("0000-GA-GOSYS_KODE6"),
    ROLE_DISKRESJONESKODE_7_READ("0000-GA-GOSYS_KODE7"),
    ROLE_EGEN_ANSATT_READ("0000-GA-PIP_EGENANSATT");

    private ArrayList<String> adRoles = new ArrayList<>();

    UserRole(String... name){
        adRoles.addAll(newArrayList(name));
    }

    @Override
    public String getAuthority() {
        return name();
    }

    public List<String> getADRolesForEnum(){
        return adRoles;
    }
}
