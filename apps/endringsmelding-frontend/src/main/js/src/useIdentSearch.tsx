import useSWR from 'swr';
import _ from 'lodash';
import originalFetch from 'isomorphic-fetch';
import { Argument } from 'classnames';

type IdentSearchResponse = [
  {
    ident: string;
    miljoer: string[];
  },
];
const fetcher = (...args: Argument[]) =>
  originalFetch(...args)
    .then((res) => {
      return res.data;
    })
    .catch((reason) => {
      if (reason?.response?.status === 401 || reason?.response?.status === 403) {
        console.error('Auth feilet');
      }
      if (reason.status === 404 || reason.response?.status === 404) {
        if (reason.response?.data?.error) {
          throw new Error(reason.response?.data?.error);
        }
      }
      throw new Error(`Henting av data fra endringsmelding-service feilet.`);
    });

export const useIdentSearch = (ident: string) => {
  const { data, isLoading, error } = useSWR<IdentSearchResponse, Error>(
    ident && `/endringsmelding-service/api/v1/ident/miljoer?ident=${ident}`,
    fetcher,
  );

  return {
    miljoer: _.isEmpty(data) ? [] : data[0]?.miljoer,
    loading: isLoading,
    error: error,
  };
};
