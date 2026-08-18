import React from 'react';
import type { Meta, StoryObj } from '@storybook/react-vite';
import HeaderLink from './HeaderLink';

const meta = {
  title: 'Header/HeaderLink',
  component: HeaderLink,
  args: {
    href: '#oversikt',
    children: 'Oversikt',
    isActive: () => false,
  },
  parameters: {
    docs: {
      description: {
        component: 'Navigasjonslenke til bruk i Dolly-headeren.',
      },
    },
  },
  argTypes: {
    href: {
      control: 'text',
      description: 'Målet lenken navigerer til.',
    },
    children: {
      control: 'text',
      description: 'Lenketeksten som vises til brukeren.',
    },
    isActive: {
      control: false,
      description: 'Avgjør om lenken skal vises som aktiv.',
    },
  },
  decorators: [
    (Story) => (
      <div style={{ backgroundColor: '#3e3832', display: 'inline-flex' }}>
        <Story />
      </div>
    ),
  ],
} satisfies Meta<typeof HeaderLink>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {};

export const Active: Story = {
  args: {
    isActive: () => true,
  },
};
