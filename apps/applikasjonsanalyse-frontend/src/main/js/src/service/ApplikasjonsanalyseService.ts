import { Api } from '@navikt/dolly-lib';

export type Avhengighet = {
  name: string;
  external: boolean;
};

export type Applikasjonsanalyse = {
  applicationName: string;
  dependencies: Avhengighet[];
};

export const fetchDependencies = () =>
  Api.fetchJson<Applikasjonsanalyse[]>('/applikasjonsanalyse-service/api/v1/dependencies', {
    method: 'GET',
  });
