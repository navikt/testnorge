import { Api } from '@navikt/dolly-lib';

const fetchBilde = () => Api.fetch('/testnorge-profil-api/api/v1/profil/bilde', { method: 'GET' });

const fetchProfil = () =>
  Api.fetchJson<{ visningsNavn: string }>('/testnorge-profil-api/api/v1/profil', { method: 'GET' });

export default { fetchBilde, fetchProfil };
