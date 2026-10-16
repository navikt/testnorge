package no.nav.dolly.synt.meldekort.bean;

import java.util.List;
import java.util.Map;

public record BeanModelDocument(
        int formatVersion,
        String meldegruppe,
        boolean strictBoundaries,
        List<Integer> visitSequence,
        List<ColumnDocument> columns
) {

    public record ColumnDocument(
            String name,
            String datatype,
            boolean categorical,
            boolean integerType,
            List<Object> labels,
            ExcludedDocument excluded,
            SynthesisDocument synthesis
    ) {
    }

    public record ExcludedDocument(
            List<Object> raw,
            List<String> output
    ) {
    }

    public record SynthesisDocument(
            String method,
            WeightedValuesDocument categorical,
            KdeDocument kde,
            CartDocument cart,
            Integer decimals,
            Double min,
            Double max,
            MissingDocument missing
    ) {
    }

    public record WeightedValuesDocument(
            List<Object> values,
            double[] weights
    ) {
    }

    public record KdeDocument(
            String kernel,
            double bandwidth,
            double[] points,
            double[] weights
    ) {
    }

    public record CartDocument(
            TreeDocument tree,
            int[] dependsOn,
            Map<Integer, LeafDocument> leaves
    ) {
    }

    public record TreeDocument(
            int[] childrenLeft,
            int[] childrenRight,
            int[] feature,
            double[] threshold,
            boolean[] missingGoToLeft
    ) {
    }

    public record LeafDocument(
            WeightedValuesDocument categorical,
            KdeDocument kde
    ) {
    }

    public record MissingDocument(
            WeightedValuesDocument categorical,
            CartDocument cart
    ) {
    }

}
