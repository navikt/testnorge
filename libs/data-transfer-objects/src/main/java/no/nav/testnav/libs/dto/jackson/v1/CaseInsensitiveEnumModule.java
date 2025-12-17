package no.nav.testnav.libs.dto.jackson.v1;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;

public class CaseInsensitiveEnumModule extends Module {

    @Override
    public String getModuleName() {
        return "CaseInsensitiveEnumModule";
    }

    @Override
    public Version version() {
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(SetupContext context) {
        SimpleDeserializers deserializers = new SimpleDeserializers();
        deserializers.addDeserializer(Enum.class, new CaseInsensitiveEnumDeserializer());
        context.addDeserializers(deserializers);
    }
}

