package no.nav.dolly.libs.syntbean;

import java.util.Arrays;
import java.util.random.RandomGenerator;

import static java.util.Objects.isNull;

public class KernelDensitySampler {

    private enum Kernel {
        GAUSSIAN,
        TOPHAT
    }

    private final Kernel kernel;
    private final double bandwidth;
    private final double[] points;
    private final CumulativeWeights weights;

    public KernelDensitySampler(BeanModelDocument.KdeDocument document) {

        if (document == null || document.points() == null || document.points().length == 0) {
            throw new IllegalArgumentException("Kernel density has no points");
        }
        this.kernel = switch (String.valueOf(document.kernel())) {
            case "gaussian" -> Kernel.GAUSSIAN;
            case "tophat" -> Kernel.TOPHAT;
            default -> throw new IllegalArgumentException("Unsupported kernel " + document.kernel());
        };
        if (!(document.bandwidth() > 0) || Double.isInfinite(document.bandwidth())) {
            throw new IllegalArgumentException("Kernel density bandwidth must be a positive number");
        }
        this.bandwidth = document.bandwidth();
        this.points = document.points().clone();
        this.weights = new CumulativeWeights(isNull(document.weights()) ? uniformWeights(points.length) : document.weights());
        if (weights.size() != points.length) {
            throw new IllegalArgumentException("Kernel density must have one weight per point");
        }

    }

    public double sample(RandomGenerator random) {

        var point = points[weights.sampleIndex(random)];
        return switch (kernel) {
            case GAUSSIAN -> point + random.nextGaussian() * bandwidth;
            case TOPHAT -> point + (random.nextDouble() * 2.0 - 1.0) * bandwidth;
        };

    }

    private static double[] uniformWeights(int size) {

        var uniform = new double[size];
        Arrays.fill(uniform, 1.0);
        return uniform;

    }

}
