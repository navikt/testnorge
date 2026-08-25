import type { Meta, StoryObj } from '@storybook/react-vite';
import SuccessAlert from './SuccessAlert';

const meta = {
  title: 'Alert/SuccessAlert',
  component: SuccessAlert,
  parameters: {
    docs: {
      description: {
        component: 'Viser en kompakt bekreftelse med suksessindikator.',
      },
    },
  },
  argTypes: {
    label: {
      control: 'text',
      description: 'Bekreftelsen som vises til brukeren.',
    },
  },
} satisfies Meta<typeof SuccessAlert>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    label: 'Endringen ble lagret',
  },
};
