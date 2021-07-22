import { Api } from '@navikt/dolly-lib';

const fetchPerson = (ident: string, miljoe: string) =>
  Api.fetchJson<Person>(`/testnav-person-service/api/v1/personer/${ident}`, {
    headers: {
      miljoe,
      persondatasystem: 'TPS',
    },
    method: 'GET',
  });

export default { fetchPerson };
