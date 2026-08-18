import type { Meta, StoryObj } from '@storybook/react-vite';
import SuccessAlertstripe from './SuccessAlertstripe';

const meta = {
  title: 'Alertstripe/SuccessAlertstripe',
  component: SuccessAlertstripe,
  parameters: {
    docs: {
      description: {
        component: 'Viser en tydelig Aksel-bekreftelse i full bredde.',
      },
    },
  },
  argTypes: {
    label: {
      control: 'text',
      description: 'Bekreftelsen som vises til brukeren.',
    },
  },
} satisfies Meta<typeof SuccessAlertstripe>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    label: 'Opplysningene er lagret',
  },
};
