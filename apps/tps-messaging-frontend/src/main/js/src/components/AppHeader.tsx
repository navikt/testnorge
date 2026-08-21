import React from 'react';
import { InternalHeader, Spacer } from '@navikt/ds-react';
import { Link as RouterLink, useLocation } from 'react-router';
import HeaderProfile from './HeaderProfile';

const AppHeader = () => {
  const { pathname } = useLocation();

  return (
    <InternalHeader className="tps-app-header">
      <InternalHeader.Title as={RouterLink} className="tps-app-header__title" to="/">
        Meldinger til TPS
      </InternalHeader.Title>
      <Spacer />
      {pathname !== '/login' && <HeaderProfile />}
    </InternalHeader>
  );
};

export default AppHeader;
