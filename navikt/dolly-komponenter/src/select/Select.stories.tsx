import type { Meta, StoryObj } from '@storybook/react-vite';
import { fn } from 'storybook/test';
import Select from './Select';

const options = [
  { label: 'Alternativ 1', value: '1' },
  { label: 'Alternativ 2', value: '2' },
  { label: 'Alternativ 3', value: '3' },
];

const meta = {
  title: 'Form/Select',
  component: Select,
  args: {
    htmlId: 'story-select',
    label: 'Velg alternativ',
    options,
    onChange: fn(),
  },
  parameters: {
    docs: {
      description: {
        component: 'Velger ett eller flere alternativer med konsistent Dolly-styling.',
      },
    },
  },
  argTypes: {
    htmlId: {
      control: 'text',
      description: 'Unik ID som kobler etiketten til skjemafeltet.',
    },
    label: {
      control: 'text',
      description: 'Etiketten som beskriver feltet.',
    },
    multi: {
      control: 'boolean',
      description: 'Tillater valg av flere alternativer.',
    },
    options: {
      control: 'object',
      description: 'Alternativene brukeren kan velge mellom.',
    },
    error: {
      control: 'text',
      description: 'Valideringsfeil som vises under feltet.',
    },
    onChange: {
      control: false,
      description: 'Kalles med valgte verdier når utvalget endres.',
    },
  },
} satisfies Meta<typeof Select>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {};

export const MultiSelect: Story = {
  args: {
    multi: true,
  },
};

export const WithError: Story = {
  args: {
    error: 'Du må velge et alternativ',
  },
};
