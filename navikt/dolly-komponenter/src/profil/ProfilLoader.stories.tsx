import React from 'react';
import type { Meta, StoryObj } from '@storybook/react-vite';
import ProfilLoader from './ProfilLoader';

type ProfilLoaderExampleProps = {
  visningsnavn: string;
  bildeUrl: string;
};

const ProfilLoaderExample = ({ visningsnavn, bildeUrl }: ProfilLoaderExampleProps) => (
  <ProfilLoader
    key={`${visningsnavn}-${bildeUrl}`}
    fetchProfil={() => Promise.resolve({ visningsNavn: visningsnavn })}
    fetchBilde={() => Promise.resolve({ url: bildeUrl })}
  />
);

const meta = {
  title: 'Profil/ProfilLoader',
  component: ProfilLoaderExample,
  parameters: {
    docs: {
      description: {
        component: 'Henter profilnavn og profilbilde asynkront før Profil vises.',
      },
    },
  },
  args: {
    visningsnavn: 'Ola Nordmann',
    bildeUrl: '',
  },
  argTypes: {
    visningsnavn: {
      control: 'text',
      description: 'Navnet som returneres fra profileksempelet.',
    },
    bildeUrl: {
      control: 'text',
      description: 'Bilde-URL som returneres fra profileksempelet.',
    },
  },
  decorators: [
    (Story) => (
      <div style={{ backgroundColor: '#3e3832', display: 'inline-block' }}>
        <Story />
      </div>
    ),
  ],
} satisfies Meta<typeof ProfilLoaderExample>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {};
