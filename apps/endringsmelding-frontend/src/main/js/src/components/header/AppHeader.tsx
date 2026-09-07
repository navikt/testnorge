import React, { useEffect, useState } from 'react';
import { Hide, HStack, InternalHeader, Spacer } from '@navikt/ds-react';
import { Link as RouterLink, useLocation } from 'react-router';
import styled from 'styled-components';
import navLogo from '@/assets/nav-logo.svg';
import ProfilService from '@/service/ProfilService';

const Header = styled(InternalHeader)`
  @media (max-width: 767px) {
    .endringsmelding-header__title {
      min-width: 0;
      padding-inline: var(--ax-space-16);
    }

    .endringsmelding-header__user {
      min-width: 0;
      max-width: 10rem;
      padding-inline: var(--ax-space-12);
      overflow: hidden;
    }

    .endringsmelding-header__user > div {
      min-width: 0;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
`;

export const AppHeader = () => {
  const { pathname } = useLocation();
  const [visningsnavn, setVisningsnavn] = useState('Laster profil...');

  useEffect(() => {
    let isMounted = true;

    ProfilService.fetchProfil()
      .then(({ visningsNavn }) => {
        if (!isMounted) {
          return;
        }

        setVisningsnavn(visningsNavn?.trim() || 'Ukjent bruker');
      })
      .catch(() => {
        if (!isMounted) {
          return;
        }

        setVisningsnavn('Profil utilgjengelig');
      });

    return () => {
      isMounted = false;
    };
  }, []);

  return (
    <Header>
      <InternalHeader.Title
        as={RouterLink}
        aria-label="NAV – Endringsmeldinger"
        className="endringsmelding-header__title"
        to="/"
      >
        <HStack align="center" gap="space-16">
          <img alt="" height={20} src={navLogo} width={64} />
          <Hide below="md">Endringsmeldinger</Hide>
        </HStack>
      </InternalHeader.Title>
      <Spacer />
      {pathname !== '/login' && (
        <InternalHeader.User
          className="endringsmelding-header__user"
          name={visningsnavn}
        />
      )}
    </Header>
  );
};
