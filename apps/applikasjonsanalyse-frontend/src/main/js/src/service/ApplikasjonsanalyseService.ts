import Api from '@/api';

export type Avhengighet = {
  name: string;
  external: boolean;
};

export type Applikasjonsanalyse = {
  applicationName: string;
  dependencies: Avhengighet[];
};

export const fetchDependencies = () =>
  Api.fetchJson<Applikasjonsanalyse[]>('/api/v1/dependencies', { method: 'GET' });
