import type { Meta, StoryObj } from '@storybook/react-vite';
import Loading from './Loading';

const meta = {
  title: 'Feedback/Loading',
  component: Loading,
  parameters: {
    docs: {
      description: {
        component: 'Viser en lasteindikator mens brukeren venter på innhold.',
      },
    },
  },
  args: {
    size: 'medium',
    title: 'Laster innhold',
    transparent: false,
    variant: 'neutral',
  },
  argTypes: {
    size: {
      control: 'select',
      options: ['xsmall', 'small', 'medium', 'large', 'xlarge', '2xlarge', '3xlarge'],
      description: 'Størrelsen på lasteindikatoren.',
    },
    title: {
      control: 'text',
      description: 'Tilgjengelig tekst som beskriver hva brukeren venter på.',
    },
    transparent: {
      control: 'boolean',
      description: 'Fjerner bakgrunnen fra lasteindikatoren.',
    },
    variant: {
      control: 'select',
      options: ['neutral', 'interaction', 'inverted'],
      description: 'Fargevarianten til lasteindikatoren.',
    },
  },
} satisfies Meta<typeof Loading>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {};
