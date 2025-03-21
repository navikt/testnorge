import {Api} from '@navikt/dolly-lib';

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


function myFunc(total: Map, value: string) {
    return total[value] = value;
}

export const getQueues = (): Promise<Map> =>
    Api.fetch(
        '/tps-messaging-service/api/v1/xml/',
        {
            method: 'GET',
            headers: {'Content-Type': 'application/json'},
        }).then((response) => response.json)
        .then((response: (string[])) => response.reduce(myFunc)) as Promise<Map>;

export const sendTpsMelding = (
    queue: string,
    message: string,
): Promise<string> =>
    Api.fetch(
        '/tps-messaging-service/api/v1/xml?queue=' + queue,
        {
            method: 'POST',
            headers: {'Content-Type': 'application/text'},
            message
        },
    ).then((response) => response) as Promise<string>;


export const slettDodsmelding = (
    ident: string,
    miljoer: string[],
): Promise<EndringsmeldingResponse> =>
    Api.fetch(
        '/endringsmelding-service/api/v2/endringsmelding/doedsmelding',
        {
            method: 'DELETE',
            headers: {miljoer: miljoer.join(','), 'Content-Type': 'application/json'},
        },
        JSON.stringify({ident: ident}),
    ).then((response) => response.json()) as Promise<EndringsmeldingResponse>;

export const sendDodsmelding = (
    dodsmelding: Dodsmelding,
    miljoer: string[],
): Promise<EndringsmeldingResponse> =>
    Api.fetch(
        '/endringsmelding-service/api/v2/endringsmelding/doedsmelding',
        {method: 'POST', headers: {miljoer: miljoer.join(','), 'Content-Type': 'application/json'}},
        JSON.stringify(dodsmelding),
    ).then((response) => response.json()) as Promise<EndringsmeldingResponse>;

export const sendFodselsmelding = (
    fodselsmelding: Fodselsmelding,
    miljoer: string[],
): Promise<EndringsmeldingResponse> =>
    Api.fetch(
        '/endringsmelding-service/api/v2/endringsmelding/foedselsmelding',
        {method: 'POST', headers: {miljoer: miljoer.join(','), 'Content-Type': 'application/json'}},
        JSON.stringify(fodselsmelding),
    ).then((response) => response.json()) as Promise<EndringsmeldingResponse>;
