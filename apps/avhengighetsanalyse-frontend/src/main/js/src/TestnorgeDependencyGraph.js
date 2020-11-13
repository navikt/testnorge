import React from 'react';
import {Graph} from "react-d3-graph";
import {LoadableComponent} from "./component"
import * as Api from "./api"

const myConfig = {
    nodeHighlightBehavior: true,
    directed: true,
    collapsible: true,
    d3: {
        gravity: -800,
    },
    node: {
        fontSize: 12,
        color: "lightgreen",
        size: 350,
        highlightStrokeColor: "blue",
        highlightFontSize: 14
    },
    link: {
        highlightColor: "red",
        highlightFontSize: 8,
    },
    width: 1200,
    height: 800,
    focusZoom: 1
};

const TestnorgeDependencyGraph = () => (
    <LoadableComponent
        onFetch={Api.fetchDependencies}
        render={data => (
            <div className="frame">
                <Graph
                    id="testnorge-dependency-graph"
                    data={data}
                    config={myConfig}
                />
            </div>
        )}

    />
);

export default TestnorgeDependencyGraph;