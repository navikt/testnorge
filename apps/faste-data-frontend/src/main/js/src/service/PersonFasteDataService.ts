import { Api } from '@navikt/dolly-lib';

const fetchPersoner = (gruppe: string, tag?: string, opprinnelse?: string) =>
  Api.fetchJson<Person[]>(
    `/testnav-person-faste-data-service/api/v1/personer?gruppe=${gruppe}${
      tag ? '&tag=' + tag : ''
    }${opprinnelse ? '&opprinnelse=' + opprinnelse : ''}`,
    {
      method: 'GET',
    }
  );

const fetchPerson = (ident: string) =>
  Api.fetchJson<Person>(`/testnav-person-faste-data-service/api/v1/organisasjoner/${ident}`, {
    method: 'GET',
  });

export default { fetchPersoner, fetchPerson };
