import React from 'react';
import { InternalHeader, Spacer } from '@navikt/ds-react';
import HeaderProfile from './HeaderProfile';

const AppHeader = () => (
  <InternalHeader className="tps-app-header">
    <InternalHeader.Title as="h1" className="tps-app-header__title">
      Meldinger til TPS
    </InternalHeader.Title>
    <Spacer />
    <HeaderProfile />
  </InternalHeader>
);

export default AppHeader;
