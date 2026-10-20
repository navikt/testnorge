import React from 'react';
import { Hide, HStack, InternalHeader, Spacer } from '@navikt/ds-react';
import { Link as RouterLink, useLocation } from 'react-router';
import navLogo from '@/assets/nav-logo.svg';
import HeaderProfile from './HeaderProfile';

const AppHeader = () => {
  const { pathname } = useLocation();

  return (
    <InternalHeader className="tps-app-header">
      <InternalHeader.Title
        as={RouterLink}
        aria-label="NAV – Meldinger til TPS"
        className="tps-app-header__title"
        to="/"
      >
        <HStack align="center" gap="space-16">
          <img alt="" height={20} src={navLogo} width={64} />
          <Hide below="md">Meldinger til TPS</Hide>
        </HStack>
      </InternalHeader.Title>
      <Spacer />
      {pathname !== '/login' && <HeaderProfile />}
    </InternalHeader>
  );
};

export default AppHeader;
