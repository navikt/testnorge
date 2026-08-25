import React from 'react';
import type { Meta, StoryObj } from '@storybook/react-vite';
import Profil from './Profil';

const meta = {
  title: 'Profil/Profil',
  component: Profil,
  parameters: {
    docs: {
      description: {
        component: 'Viser profilbilde og valgfritt visningsnavn i Dolly-headeren.',
      },
    },
  },
  argTypes: {
    url: {
      control: 'text',
      description: 'URL til profilbildet. Dolly-logoen brukes når feltet er tomt.',
    },
    visningsnavn: {
      control: 'text',
      description: 'Navnet som vises ved siden av profilbildet.',
    },
  },
  decorators: [
    (Story) => (
      <div style={{ backgroundColor: '#3e3832', display: 'inline-block' }}>
        <Story />
      </div>
    ),
  ],
} satisfies Meta<typeof Profil>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    visningsnavn: 'Ola Nordmann',
  },
};
