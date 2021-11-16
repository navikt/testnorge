import React from 'react';
import { Page } from '@navikt/dolly-komponenter';
import { AccessList } from '@/components/AccessList';
import AccessFrom from '@/components/AccessFrom/AccessFrom';

const HomePage = () => {
  return (
    <Page>
      <h2>Gi organisasjon tilgang til Dolly</h2>
      <AccessFrom />
      <h2>Organisasjoner som har tilgang til Dolly</h2>
      <AccessList />
    </Page>
  );
};

HomePage.displayName = 'HomePage';

export default HomePage;
