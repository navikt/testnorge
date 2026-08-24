import type { Meta, StoryObj } from '@storybook/react-vite';
import ErrorAlertstripe from './ErrorAlertstripe';

const meta = {
  title: 'Alertstripe/ErrorAlertstripe',
  component: ErrorAlertstripe,
  parameters: {
    docs: {
      description: {
        component: 'Viser en tydelig Aksel-feilmelding i full bredde.',
      },
    },
  },
  argTypes: {
    label: {
      control: 'text',
      description: 'Feilmeldingen som vises til brukeren.',
    },
  },
} satisfies Meta<typeof ErrorAlertstripe>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    label: 'Kunne ikke hente data',
  },
};
