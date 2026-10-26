package no.nav.dolly.libs.syntbean;

class DecisionTree {

    private static final int TREE_LEAF = -1;

    private final int[] childrenLeft;
    private final int[] childrenRight;
    private final int[] feature;
    private final double[] threshold;
    private final boolean[] missingGoToLeft;
    private final int featureCount;

    DecisionTree(BeanModelDocument.TreeDocument document) {

        if (document == null || document.childrenLeft() == null || document.childrenLeft().length == 0) {
            throw new IllegalArgumentException("Decision tree has no nodes");
        }
        var nodeCount = document.childrenLeft().length;
        if (document.childrenRight() == null || document.childrenRight().length != nodeCount
                || document.feature() == null || document.feature().length != nodeCount
                || document.threshold() == null || document.threshold().length != nodeCount
                || (document.missingGoToLeft() != null && document.missingGoToLeft().length != nodeCount)) {
            throw new IllegalArgumentException("Decision tree arrays must have the same length");
        }
        this.childrenLeft = document.childrenLeft().clone();
        this.childrenRight = document.childrenRight().clone();
        this.feature = document.feature().clone();
        this.threshold = document.threshold().clone();
        this.missingGoToLeft = document.missingGoToLeft() == null ? new boolean[nodeCount] : document.missingGoToLeft().clone();
        this.featureCount = validateNodes(nodeCount);

    }

    int featureCount() {
        return featureCount;
    }

    int apply(double[] features) {

        var node = 0;
        while (childrenLeft[node] != TREE_LEAF) {
            var value = (float) features[feature[node]];
            if (Float.isNaN(value)) {
                node = missingGoToLeft[node] ? childrenLeft[node] : childrenRight[node];
            } else if (value <= threshold[node]) {
                node = childrenLeft[node];
            } else {
                node = childrenRight[node];
            }
        }
        return node;

    }

    private int validateNodes(int nodeCount) {

        var maxFeature = -1;
        for (int node = 0; node < nodeCount; node++) {
            if (childrenLeft[node] == TREE_LEAF) {
                continue;
            }
            if (!isChild(childrenLeft[node], node, nodeCount) || !isChild(childrenRight[node], node, nodeCount) || feature[node] < 0) {
                throw new IllegalArgumentException("Decision tree node " + node + " is invalid");
            }
            maxFeature = Math.max(maxFeature, feature[node]);
        }
        return maxFeature + 1;

    }

    private static boolean isChild(int child, int parent, int nodeCount) {
        return child > parent && child < nodeCount;
    }

}
