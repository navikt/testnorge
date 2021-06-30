import React from 'react';
import { Graph } from 'react-d3-graph';
import { Applikasjonsanalyse } from '@/service/ApplikasjonsanalyseService';

const convert = (data: Applikasjonsanalyse[]) => {
  const application = new Map();
  data.forEach((app: any) => {
    application.set(app.applicationName, {
      id: app.applicationName,
      name: app.applicationName,
    });
    app.dependencies.forEach((dependency: any) =>
      application.set(dependency.name, {
        id: dependency.name,
        color: dependency.external ? '#FF7F50' : 'lightgreen',
      })
    );
  });

  const links = data
    .filter((app: any) => app.dependencies.length > 0)
    .reduce((result: any, app: any) => {
      app.dependencies.forEach((dependency: any) => {
        result.push({
          source: app.applicationName,
          target: dependency.name,
        });
      });
      return result;
    }, []);
  return {
    nodes: Array.from(application.values()),
    links,
  };
};

type Props = {
  data: Applikasjonsanalyse[];
};

const myConfig = {
  nodeHighlightBehavior: true,
  directed: true,
  collapsible: true,
  d3: {
    gravity: -800,
  },
  node: {
    fontSize: 12,
    color: 'lightgreen',
    size: 350,
    highlightStrokeColor: 'blue',
    highlightFontSize: 14,
  },
  link: {
    highlightColor: 'red',
    highlightFontSize: 8,
  },
  width: 1200,
  height: 800,
  focusZoom: 1,
};

export default ({ data }: Props) => (
  <Graph
    id="testnorge-dependency-graph"
    data={convert(
      data.filter(
        (value) =>
          value.applicationName !== 'team-dolly-lokal-app' &&
          value.applicationName !== 'testnorge-oversikt-frontend'
      )
    )}
    config={myConfig}
  />
);
