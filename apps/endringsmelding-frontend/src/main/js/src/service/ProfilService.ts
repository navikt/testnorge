import Api from '@/api';

const fetchBilde = () => Api.fetch('/api/v1/profil/bilde', { method: 'GET' });

const fetchProfil = () =>
  Api.fetchJson<{ visningsNavn: string }>('/api/v1/profil', { method: 'GET' });

export default { fetchBilde, fetchProfil };
