import { Api } from '@navikt/dolly-lib';

type Dodsmelding = {
  ident: string;
  handling: 'SETTE_DOEDSDATO' | 'ENDRET_DOEDSDATO' | 'ANNULLERE_DOEDSDATO';
  doedsdato: string;
};

type Fodselsmelding = {
  identFar?: string;
  identMor: string;
  identtype: string;
  kjoenn: 'GUTT' | 'JENTE' | 'UKJENT';
  adresseFra: 'LAG_NY_ADRESSE' | 'ARV_FRA_MORS' | 'ARV_FRA_FARS';
  foedselsdato: string;
};

export const sendDodsmelding = (dodsmelding: Dodsmelding, miljoer: string[]) =>
  Api.fetch(
    '/endringsmelding-service/api/v1/endringsmelding/doedsmelding',
    { method: 'POST', headers: { miljoer: miljoer.join(','), 'Content-Type': 'application/json' } },
    JSON.stringify(dodsmelding)
  );

export const sendFodselsmelding = (
  fodselsmelding: Fodselsmelding,
  miljoer: string[]
): Promise<string> =>
  Api.fetch(
    '/endringsmelding-service/api/v1/endringsmelding/foedeselsmelding',
    { method: 'POST', headers: { miljoer: miljoer.join(','), 'Content-Type': 'application/json' } },
    JSON.stringify(fodselsmelding)
  ).then((response) => response.text() as Promise<string>);
