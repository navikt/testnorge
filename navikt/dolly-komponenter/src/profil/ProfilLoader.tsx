import React, { useEffect, useState } from 'react';
import Profil from './Profil';

export type ProfilLoaderProps = {
  fetchProfil: () => Promise<{ visningsNavn: string }>;
  fetchBilde: () => Promise<{ url: string }>;
};

const ProfilLoader = ({ fetchProfil, fetchBilde }: ProfilLoaderProps) => {
  const [navn, setNavn] = useState<string>();
  const [url, setUrl] = useState<string>();

  useEffect(() => {
    fetchProfil().then(({ visningsNavn }) => setNavn(visningsNavn));
    fetchBilde().then(({ url }) => setUrl(url));
  }, []);

  return <Profil visningsnavn={navn} url={url} />;
};

ProfilLoader.displayName = 'ProfilLoader';

export default ProfilLoader;
