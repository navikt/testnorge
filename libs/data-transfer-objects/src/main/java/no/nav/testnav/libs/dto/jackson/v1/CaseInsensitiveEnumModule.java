package no.nav.testnav.libs.dto.jackson.v1;

import tools.jackson.core.Version;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.module.SimpleDeserializers;

public class CaseInsensitiveEnumModule extends JacksonModule {

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
