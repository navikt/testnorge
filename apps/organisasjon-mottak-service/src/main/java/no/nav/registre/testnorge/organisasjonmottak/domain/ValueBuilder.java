package no.nav.registre.testnorge.organisasjonmottak.domain;

class ValueBuilder {
    private final StringBuilder builder;

    private ValueBuilder(String type, int length, String endringstype) {
        this.builder = createBaseStringbuilder(length, type, endringstype);
    }

    static ValueBuilder newBuilder(String type, int length, String endringstype) {
        return new ValueBuilder(type, length, endringstype);
    }

    static ValueBuilder newBuilder(String type, int length) {
        return new ValueBuilder(type, length, "N");
    }

    private static StringBuilder createBaseStringbuilder(int size, String type, String endringsType) {
        StringBuilder stringBuilder = createStringBuilderWithReplacement(size, ' ');
        stringBuilder.replace(0, type.length(), type);
        stringBuilder.replace(4, 5, endringsType);
        return stringBuilder;
    }

    private static StringBuilder createStringBuilderWithReplacement(int size, char replacement) {
        StringBuilder stringBuilder = new StringBuilder(size);
        stringBuilder.setLength(size);
        for (int i = 0; i < stringBuilder.length(); i++) {
            stringBuilder.setCharAt(i, replacement);
        }
        return stringBuilder;
    }

    ValueBuilder setLine(int exclusiveStart, String value) {
        if (value == null) {
            return this;
        }
        builder.replace(exclusiveStart, exclusiveStart + value.length(), value);
        return this;
    }

    @Override
    public String toString() {
        return builder.append("\n").toString();
    }

}
