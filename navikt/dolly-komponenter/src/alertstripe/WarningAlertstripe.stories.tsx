import type { Meta, StoryObj } from '@storybook/react-vite';
import WarningAlertstripe from './WarningAlertstripe';

const meta = {
  title: 'Alertstripe/WarningAlertstripe',
  component: WarningAlertstripe,
  parameters: {
    docs: {
      description: {
        component: 'Viser en tydelig Aksel-advarsel i full bredde.',
      },
    },
  },
  argTypes: {
    label: {
      control: 'text',
      description: 'Advarselen som vises til brukeren.',
    },
  },
} satisfies Meta<typeof WarningAlertstripe>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    label: 'Noen opplysninger mangler',
  },
};
