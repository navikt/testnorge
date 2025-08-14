package no.nav.dolly.plugins;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.util.Map;

class GitHubActionsWorkflowConstructor extends Constructor {

    GitHubActionsWorkflowConstructor() {
        super(Map.class, new LoaderOptions());
    }

    @Override
    protected Object constructObject(Node node) {
        if (node instanceof ScalarNode scalarNode && scalarNode.getValue().equals("on")) {
            return "on";
        }
        return super.constructObject(node);
    }

}
