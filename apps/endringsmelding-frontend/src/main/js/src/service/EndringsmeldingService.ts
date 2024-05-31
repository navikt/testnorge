import { Api } from '@navikt/dolly-lib';

type Dodsmelding = {
  ident: string;
  handling: 'SETTE_DOEDSDATO' | 'ENDRET_DOEDSDATO' | 'ANNULLERE_DOEDSDATO';
  doedsdato: string;
};

type EndringsmeldingResponse = {
  ident: string;
  miljoStatus?: Map<string, string>;
  error?: string;
};

type Fodselsmelding = {
  identFar?: string;
  identMor: string;
  identtype: string;
  kjoenn: 'GUTT' | 'JENTE' | 'UKJENT';
  adresseFra: 'LAG_NY_ADRESSE' | 'ARV_FRA_MORS' | 'ARV_FRA_FARS';
  foedselsdato: string;
};

export const slettDodsmelding = (
  ident: string,
  miljoer: string[],
): Promise<EndringsmeldingResponse> =>
  Api.fetch(
    '/endringsmelding-service/api/v2/endringsmelding/doedsmelding',
    {
      method: 'DELETE',
      headers: { miljoer: miljoer.join(','), 'Content-Type': 'application/json' },
    },
    JSON.stringify({ ident: ident }),
  ).then((response) => response.json()) as Promise<EndringsmeldingResponse>;

export const sendDodsmelding = (
  dodsmelding: Dodsmelding,
  miljoer: string[],
): Promise<EndringsmeldingResponse> =>
  Api.fetch(
    '/endringsmelding-service/api/v2/endringsmelding/doedsmelding',
    { method: 'POST', headers: { miljoer: miljoer.join(','), 'Content-Type': 'application/json' } },
    JSON.stringify(dodsmelding),
  ).then((response) => response.json()) as Promise<EndringsmeldingResponse>;

export const sendFodselsmelding = (
  fodselsmelding: Fodselsmelding,
  miljoer: string[],
): Promise<EndringsmeldingResponse> =>
  Api.fetch(
    '/endringsmelding-service/api/v2/endringsmelding/foedselsmelding',
    { method: 'POST', headers: { miljoer: miljoer.join(','), 'Content-Type': 'application/json' } },
    JSON.stringify(fodselsmelding),
  ).then((response) => response.json()) as Promise<EndringsmeldingResponse>;
