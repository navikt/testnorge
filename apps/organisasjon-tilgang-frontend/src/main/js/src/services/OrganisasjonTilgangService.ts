import { Api } from '@navikt/dolly-lib';

type Organisasjon = {
  navn: string;
  organisasjonsnummer: string;
  organisasjonsform: string;
  gyldigTil: string;
};

const getOrganisasjonTilganger = () =>
  Api.fetchJson<Organisasjon[]>('/organisasjon-tilgang-service/api/v1/organisasjoner', {
    method: 'GET',
  });

const createOrganisasjonTilgang = (organisasjonsnummer: string, gyldigTil: string) =>
  Api.fetchJson<Organisasjon>(
    '/organisasjon-tilgang-service/api/v1/organisasjoner',
    { method: 'POST' },
    JSON.stringify({ organisasjonsnummer, gyldigTil })
  );

const deleteOrganisasjonTilgang = (organisasjonsnummer: string) =>
  Api.fetch(`/organisasjon-tilgang-service/api/v1/organisasjoner/${organisasjonsnummer}`, {
    method: 'DELETE',
  });

export default {
  getOrganisasjonTilganger,
  createOrganisasjonTilgang,
  deleteOrganisasjonTilgang,
};
