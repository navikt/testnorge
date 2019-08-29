package no.nav.registre.sdForvalter.util.database;

import java.util.List;

public interface CreatableFromString {

    void updateFromString(List<String> input, List<String> headers);
}
