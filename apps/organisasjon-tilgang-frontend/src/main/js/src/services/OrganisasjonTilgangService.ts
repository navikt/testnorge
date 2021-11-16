import { Api } from '@navikt/dolly-lib';

type Organisasjon = {
  navn: string;
  orgnisasjonsnummer: string;
  orgnisasjonsfrom: string;
  gyldigTil: string;
};

const getOrganisasjonTilganger = () =>
  Api.fetchJson<Organisasjon[]>('/organisasjon-tilgang-service/api/v1/organisasjoner', {
    method: 'GET',
  });

const createOrganisasjonTilgang = (organisajonsnummer: string, gyldigTil: string) =>
  Api.fetchJson<Organisasjon>(
    '/organisasjon-tilgang-service/api/v1/organisasjoner',
    { method: 'POST' },
    JSON.stringify({ organisajonsnummer, gyldigTil })
  );

const deleteOrganisasjonTilgang = (organisajonsnummer: string) =>
  Api.fetch(`/organisasjon-tilgang-service/api/v1/organisasjoner/${organisajonsnummer}`, {
    method: 'DELETE',
  });

export default {
  getOrganisasjonTilganger,
  createOrganisasjonTilgang,
  deleteOrganisasjonTilgang,
};
