import type { Meta, StoryObj } from '@storybook/react-vite';
import { fn } from 'storybook/test';
import Knapp from './Knapp';

const meta = {
  title: 'Form/Knapp',
  component: Knapp,
  args: {
    children: 'Lagre',
    onClick: fn(),
  },
  parameters: {
    docs: {
      description: {
        component: 'Aksel-knapp med Dolly-bibliotekets standardplassering.',
      },
    },
  },
  argTypes: {
    children: {
      control: 'text',
      description: 'Teksten som vises i knappen.',
    },
    variant: {
      control: 'select',
      options: ['primary', 'secondary', 'tertiary', 'danger'],
      description: 'Knappens visuelle variant.',
    },
    size: {
      control: 'select',
      options: ['small', 'medium'],
      description: 'Knappens størrelse.',
    },
    disabled: {
      control: 'boolean',
      description: 'Angir om knappen er deaktivert.',
    },
    loading: {
      control: 'boolean',
      description: 'Viser lastetilstand og hindrer flere klikk.',
    },
    onClick: {
      control: false,
      description: 'Kalles når brukeren aktiverer knappen.',
    },
  },
} satisfies Meta<typeof Knapp>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {};

export const Secondary: Story = {
  args: {
    children: 'Avbryt',
    variant: 'secondary',
  },
};
