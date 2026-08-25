import type { Meta, StoryObj } from '@storybook/react-vite';
import Page from './Page';

const meta = {
  title: 'Layout/Page',
  component: Page,
  args: {
    children: 'Sideinnhold med standard marger',
  },
  parameters: {
    docs: {
      description: {
        component: 'Plasserer sideinnhold med Dolly-bibliotekets standardmarger.',
      },
    },
  },
  argTypes: {
    children: {
      control: 'text',
      description: 'Innholdet som vises på siden.',
    },
  },
} satisfies Meta<typeof Page>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {};
