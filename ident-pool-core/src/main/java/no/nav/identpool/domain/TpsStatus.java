package no.nav.identpool.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TpsStatus {
    private static final int PRIME = 31;

    private String ident;
    private boolean inUse;

    @Override
    public int hashCode() {
        int result = 1;
        result = PRIME * result + ((ident == null) ? 0 : ident.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }

        TpsStatus other = (TpsStatus) obj;
        if (ident == null) {
            return other.ident == null;
        } else {
            return ident.equals(other.ident);
        }
    }
}
