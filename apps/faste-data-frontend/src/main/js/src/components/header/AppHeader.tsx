import { CheckmarkIcon, MenuGridIcon } from '@navikt/aksel-icons';
import { ActionMenu, InternalHeader, Spacer, Theme } from '@navikt/ds-react';
import { Link as RouterLink, useLocation } from 'react-router';
import ProfilLoader from '@/components/profil/ProfilLoader';

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
    <InternalHeader>
      <InternalHeader.Title as={RouterLink} to="/">
        Faste Data
      </InternalHeader.Title>
      <Spacer />
      <ActionMenu>
        <ActionMenu.Trigger>
          <InternalHeader.Button>
            <MenuGridIcon fontSize="1.5rem" title="Navigasjon" />
          </InternalHeader.Button>
        </ActionMenu.Trigger>
        <Theme theme="light">
          <ActionMenu.Content align="end">
            <ActionMenu.Group label="Navigasjon">
              {links.map((link) => {
                const active = link.isActive(pathname);

                return (
                  <ActionMenu.Item
                    key={link.href}
                    as={RouterLink}
                    aria-current={active ? 'page' : undefined}
                    icon={active ? <CheckmarkIcon aria-hidden /> : undefined}
                    indent={!active}
                    to={link.href}
                  >
                    {link.label}
                  </ActionMenu.Item>
                );
              })}
            </ActionMenu.Group>
          </ActionMenu.Content>
        </Theme>
      </ActionMenu>
      <ProfilLoader />
    </InternalHeader>
  );
};

export default AppHeader;
