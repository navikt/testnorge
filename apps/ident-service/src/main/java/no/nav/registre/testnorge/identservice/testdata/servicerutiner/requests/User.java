package no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String name;
    private String username;
    private String token;
    private Environment environment;

    public User(String name, String username) {
        this.name = name;
        this.username = username;
    }
}
