import React from 'react';
import type { Meta, StoryObj } from '@storybook/react-vite';
import Header from './Header';
import Profil from '../profil/Profil';
import { HeaderLinkGroup } from '../header-link-group';
import { HeaderLink } from '../header-link';

const meta = {
  title: 'Header/Header',
  component: Header,
  parameters: {
    layout: 'fullscreen',
    docs: {
      description: {
        component: 'Applikasjonshode med Nav-logo, tittel, navigasjon og valgfri profil.',
      },
    },
  },
  argTypes: {
    title: {
      control: 'text',
      description: 'Applikasjonsnavnet som vises i headeren.',
    },
    children: {
      control: false,
      description: 'Valgfri navigasjon som vises etter tittelen.',
    },
    profile: {
      control: false,
      description: 'Valgfri profilkomponent som vises til høyre.',
    },
  },
} satisfies Meta<typeof Header>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    title: 'Test-Norge',
  },
};

export const WithNavigationAndProfile: Story = {
  args: {
    title: 'Test-Norge',
    profile: <Profil visningsnavn="Ola Nordmann" />,
    children: (
      <HeaderLinkGroup>
        <HeaderLink href="#oversikt" isActive={() => true}>
          Oversikt
        </HeaderLink>
        <HeaderLink href="#bestillinger" isActive={() => false}>
          Bestillinger
        </HeaderLink>
      </HeaderLinkGroup>
    ),
  },
};
