import { HStack, InternalHeader, Spacer } from '@navikt/ds-react';
import { Link as RouterLink, useLocation } from 'react-router';
import styled from 'styled-components';
import ProfilLoader from '@/components/profil/ProfilLoader';

const Header = styled(InternalHeader)`
  min-height: 4.375rem;

  .faste-data-header__title {
    min-height: 4.375rem;
    padding-inline: var(--ax-space-20);
    white-space: nowrap;
  }

  .faste-data-header__navigation-button {
    min-height: 4.375rem;
    padding-inline: var(--ax-space-20);
    white-space: nowrap;
  }

  @media (max-width: 767px) {
    min-height: 0;
    flex-wrap: wrap;

    .faste-data-header__title {
      min-height: 3.5rem;
      padding-inline: var(--ax-space-16);
    }

    .faste-data-header__navigation-button {
      min-height: 3rem;
      padding-inline: var(--ax-space-16);
    }
  }
`;

const Divider = styled.div`
  align-self: stretch;
  width: 1px;
  background: var(--ax-border-neutral-strong);

  @media (max-width: 767px) {
    display: none;
  }
`;

const Navigation = styled.nav`
  display: flex;
  align-items: stretch;
  overflow-x: auto;

  @media (max-width: 767px) {
    order: 3;
    width: 100%;
    border-top: 1px solid var(--ax-border-neutral-subtleA);
  }
`;

const NavigationRow = styled(HStack)`
  flex-wrap: nowrap;
`;

const HeaderSpacer = styled(Spacer)`
  @media (max-width: 767px) {
    display: none;
  }
`;

const ProfileSection = styled.div`
  min-width: 0;
  max-width: 16rem;

  @media (max-width: 767px) {
    order: 2;
    max-width: calc(100% - 8rem);
    margin-left: auto;
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
      <Divider aria-hidden="true" />
      <Navigation aria-label="Hovednavigasjon">
        <NavigationRow gap="space-0">
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
        </NavigationRow>
      </Navigation>
      <HeaderSpacer />
      <Divider aria-hidden="true" />
      <ProfileSection>
        <ProfilLoader />
      </ProfileSection>
    </Header>
  );
};

export default AppHeader;
