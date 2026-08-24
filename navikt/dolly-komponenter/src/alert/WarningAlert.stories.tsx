import type { Meta, StoryObj } from '@storybook/react-vite';
import WarningAlert from './WarningAlert';

const meta = {
  title: 'Alert/WarningAlert',
  component: WarningAlert,
  parameters: {
    docs: {
      description: {
        component: 'Viser en kompakt advarsel med varselsindikator.',
      },
    },
  },
  argTypes: {
    label: {
      control: 'text',
      description: 'Advarselen som vises til brukeren.',
    },
  },
} satisfies Meta<typeof WarningAlert>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    label: 'Kontroller opplysningene før du fortsetter',
  },
};
