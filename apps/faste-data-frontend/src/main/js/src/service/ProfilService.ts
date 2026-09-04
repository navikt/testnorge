import { Api } from '@navikt/dolly-lib';

const fetchProfil = () =>
  Api.fetchJson<{ visningsNavn: string }>('/testnorge-profil-api/api/v1/profil', { method: 'GET' });

export default { fetchProfil };
