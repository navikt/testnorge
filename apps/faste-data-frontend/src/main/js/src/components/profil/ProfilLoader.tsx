import React, { useEffect, useState } from 'react';
import { InternalHeader } from '@navikt/ds-react';
import ProfilService from '@/service/ProfilService';

export default () => {
  const [navn, setNavn] = useState<string>();
  const [error, setError] = useState(false);

  useEffect(() => {
    let active = true;

    ProfilService.fetchProfil()
      .then(({ visningsNavn }) => {
        if (!active) {
          return;
        }

        setNavn(visningsNavn?.trim() || 'Ukjent bruker');
      })
      .catch(() => {
        if (!active) {
          return;
        }
        setError(true);
      });

    return () => {
      active = false;
    };
  }, []);

  const name = error ? 'Profil utilgjengelig' : navn ?? 'Laster profil...';

  return <InternalHeader.User name={name} />;
};
