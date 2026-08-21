import { InternalHeader, Spacer } from '@navikt/ds-react';
import { Link as RouterLink, useLocation } from 'react-router';
import styled from 'styled-components';
import ProfilLoader from '@/components/profil/ProfilLoader';

const Header = styled(InternalHeader)`
  @media (max-width: 767px) {
    flex-wrap: wrap;

    .faste-data-header__title {
      min-height: var(--ax-space-48);
      padding-inline: var(--ax-space-16);
    }

    .faste-data-header__navigation-button {
      min-height: var(--ax-space-48);
      padding-inline: var(--ax-space-16);
    }

    .faste-data-header__user {
      min-height: var(--ax-space-48);
    }
  }
`;

const Navigation = styled.nav`
  display: flex;
  align-items: stretch;
  overflow-x: auto;

  @media (max-width: 767px) {
    order: 3;
    width: 100%;
    min-height: var(--ax-space-48);
    border-top: 1px solid var(--ax-border-neutral-subtleA);
  }
`;

const HeaderSpacer = styled(Spacer)`
  @media (max-width: 767px) {
    display: none;
  }
`;

const links = [
  {
    href: '/',
    label: 'Hjem',
    isActive: (pathname: string) => pathname === '/',
  },
  {
    href: '/person',
    label: 'Person',
    isActive: (pathname: string) => pathname === '/person',
  },
  {
    href: '/organisasjon',
    label: 'Organisasjon',
    isActive: (pathname: string) => pathname.startsWith('/organisasjon'),
  },
];

const AppHeader = () => {
  const { pathname } = useLocation();

  return (
    <Header>
      <InternalHeader.Title as={RouterLink} className="faste-data-header__title" to="/">
        Faste Data
      </InternalHeader.Title>
      <Navigation aria-label="Hovednavigasjon">
        {links.map((link) => {
          const active = link.isActive(pathname);
          return (
            <InternalHeader.Button
              key={link.href}
              as={RouterLink}
              aria-current={active ? 'page' : undefined}
              className="faste-data-header__navigation-button"
              isActive={active}
              to={link.href}
            >
              {link.label}
            </InternalHeader.Button>
          );
        })}
      </Navigation>
      <HeaderSpacer />
      <ProfilLoader />
    </Header>
  );
};

export default AppHeader;
