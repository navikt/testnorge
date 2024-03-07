import useSWR from 'swr';
import axios from 'axios';

const fetcher = (url, headers) =>
  axios
    .get(url, { headers: headers })
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
      throw new Error(`Henting av data fra ${url} feilet.`);
    });

export const useIdentSearch = (ident) => {
  const { data, isLoading, error } = useSWR<any, Error>(
    ident && `/endringsmelding-service/api/v1/ident/miljoer?ident=${ident}`,
    fetcher,
  );

  return {
    identer: data,
    loading: isLoading,
    error: error,
  };
};
