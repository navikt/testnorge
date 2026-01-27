package no.nav.testnav.pdllagreservice.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class PdlPersonMappingAddendumTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void appendSizeAttribute_OK() throws Exception {

        val dokument = objectMapper.readValue(getJsonPerson(), new TypeReference<HashMap<String, Object>>() {
        });

        val result = PdlPersonMappingAddendum.appendSizeAttribute(dokument);
        val persondataljer = (HashMap<String, Object>) result.get("hentPerson");

        assertThat(((List<Map<String, Object>>) persondataljer.get("bostedsadresse")).getFirst(),
                hasEntry(is("size"), is(3)));
        assertThat(((List<Map<String, Object>>) persondataljer.get("sivilstand")).getFirst(),
                hasEntry(is("size"), is(1)));
        assertThat(((List<Map<String, Object>>) persondataljer.get("statsborgerskap")).getFirst(),
                hasEntry(is("size"), is(1)));
        assertThat(((List<Map<String, Object>>) persondataljer.get("navn")).getFirst(),
                hasEntry(is("size"), is(1)));
        assertThat(((List<Map<String, Object>>) persondataljer.get("kjoenn")).getFirst(),
                hasEntry(is("size"), is(1)));
        assertThat(((List<Map<String, Object>>) persondataljer.get("foedested")).getFirst(),
                hasEntry(is("size"), is(1)));
        assertThat(((List<Map<String, Object>>) persondataljer.get("foedselsdato")).getFirst(),
                hasEntry(is("size"), is(1)));
        assertThat(((List<Map<String, Object>>) persondataljer.get("folkeregisteridentifikator")).getFirst(),
                hasEntry(is("size"), is(1)));
        assertThat(((List<Map<String, Object>>) persondataljer.get("folkeregisterpersonstatus")).getFirst(),
                hasEntry(is("size"), is(1)));
    }

    private File getJsonPerson() {

        return new File(requireNonNull(getClass().getResource("/testdata/person.json")).getFile());
    }
}