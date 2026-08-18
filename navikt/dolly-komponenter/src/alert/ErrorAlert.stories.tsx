import type { Meta, StoryObj } from '@storybook/react-vite';
import ErrorAlert from './ErrorAlert';

const meta = {
  title: 'Alert/ErrorAlert',
  component: ErrorAlert,
  parameters: {
    docs: {
      description: {
        component: 'Viser en kompakt feilmelding med feilindikator.',
      },
    },
  },
  argTypes: {
    label: {
      control: 'text',
      description: 'Feilmeldingen som vises til brukeren.',
    },
  },
} satisfies Meta<typeof ErrorAlert>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    label: 'En feil har oppstått',
  },
};
