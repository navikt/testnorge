import { Api } from '@navikt/dolly-lib';

export type Organisasjon = {
  orgnummer: string;
  enhetType: string;
  navn: string;
  juridiskEnhet?: string;
  redigertnavn?: string;
  forretningsAdresse?: Adresse;
  postadresse?: Adresse;
  driverVirksomheter: string[];
};

export type Adresse = {
  adresselinje1?: string;
  adresselinje2?: string;
  adresselinje3?: string;
  postnr?: string;
  kommunenr?: string;
  landkode?: string;
  poststed?: string;
};

const fetchOrganisasjon = (orgnummer: string, miljo: string) =>
  Api.fetchJson<Organisasjon>(`/testnav-organisasjon-service/api/v1/organisasjoner/${orgnummer}`, {
    headers: {
      miljo,
    },
    method: 'GET',
  });

export default { fetchOrganisasjon };
