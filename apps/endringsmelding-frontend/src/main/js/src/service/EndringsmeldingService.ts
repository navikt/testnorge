import Api from '@/api';

const fetchMiljoer = (ident: String) =>
  Api.fetchJson<string[]>(`/api/v1/identer/${ident}/miljoer`, { method: 'GET' });

export default {
  fetchMiljoer,
};
