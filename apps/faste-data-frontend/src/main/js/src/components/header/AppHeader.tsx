import { MenuGridIcon } from '@navikt/aksel-icons';
import { ActionMenu, InternalHeader, Spacer, Theme } from '@navikt/ds-react';
import { Link as RouterLink } from 'react-router';
import ProfilLoader from '@/components/profil/ProfilLoader';

const links = [
  {
    href: '/',
    label: 'Hjem',
  },
  {
    href: '/person',
    label: 'Person',
  },
  {
    href: '/organisasjon',
    label: 'Organisasjon',
  },
];

const AppHeader = () => {
  return (
    <InternalHeader>
      <InternalHeader.Title as={RouterLink} to="/">
        Faste Data
      </InternalHeader.Title>
      <Spacer />
      <ActionMenu>
        <ActionMenu.Trigger>
          <InternalHeader.Button>
            <MenuGridIcon style={{ fontSize: '1.5rem' }} title="Applikasjonsmeny" />
          </InternalHeader.Button>
        </ActionMenu.Trigger>
        <Theme theme="light">
          <ActionMenu.Content align="end">
            <ActionMenu.Group label="Navigasjon">
              {links.map((link) => (
                <ActionMenu.Item key={link.href} as={RouterLink} to={link.href}>
                  {link.label}
                </ActionMenu.Item>
              ))}
            </ActionMenu.Group>
          </ActionMenu.Content>
        </Theme>
      </ActionMenu>
      <ProfilLoader />
    </InternalHeader>
  );
};

export default AppHeader;
