import React from 'react';
import { InternalHeader } from '@navikt/ds-react';
import ProfilService from '../service/ProfilService';

const HeaderProfile = () => {
  const [name, setName] = React.useState('Laster profil...');

  React.useEffect(() => {
    let isMounted = true;

    ProfilService.fetchProfil()
      .then(({ visningsNavn }) => {
        if (!isMounted) {
          return;
        }

        setName(visningsNavn?.trim() || 'Ukjent bruker');
      })
      .catch(() => {
        if (isMounted) {
          setName('Ukjent bruker');
        }
      });

    return () => {
      isMounted = false;
    };
  }, []);

  return <InternalHeader.User className="tps-app-header__user" name={name} />;
};

export default HeaderProfile;
