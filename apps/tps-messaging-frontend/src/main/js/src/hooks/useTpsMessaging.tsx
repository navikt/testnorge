import useSWR from 'swr';
import { fetcher } from '../api/api';

const tpsMessagingUrl = '/tps-messaging-service/api/v1/xml';

export const useTpsMessagingXml = () => {
  const { data, isLoading, error } = useSWR<string[], Error>(tpsMessagingUrl, fetcher, {
    dedupingInterval: 2000,
  });

  return {
    queues: data?.map((queue) => ({
      label: queue,
      value: queue,
    })),
    loading: isLoading,
    error: error,
  };
};
