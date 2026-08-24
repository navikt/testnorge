import React from 'react';
import type { Meta, StoryObj } from '@storybook/react-vite';
import { Form, InputFormItem, Line, SelectFormItem } from './index';

const options = [
  { label: 'Alternativ 1', value: '1' },
  { label: 'Alternativ 2', value: '2' },
];

type FormExampleProps = {
  nameLabel: string;
  selectLabel: string;
  reverse: boolean;
  disabled: boolean;
};

const FormExample = ({ nameLabel, selectLabel, reverse, disabled }: FormExampleProps) => (
  <Form>
    <Line reverse={reverse}>
      <InputFormItem label={nameLabel} disabled={disabled} />
      <SelectFormItem
        htmlId="story-form-select"
        label={selectLabel}
        options={options}
        onChange={() => undefined}
        disabled={disabled}
      />
    </Line>
  </Form>
);

const meta = {
  title: 'Form/Form',
  component: FormExample,
  parameters: {
    docs: {
      description: {
        component: 'Viser Form, Line, InputFormItem og SelectFormItem i et samlet skjemaoppsett.',
      },
    },
  },
  args: {
    nameLabel: 'Navn',
    selectLabel: 'Alternativ',
    reverse: false,
    disabled: false,
  },
  argTypes: {
    nameLabel: {
      control: 'text',
      description: 'Etiketten til tekstfeltet.',
    },
    selectLabel: {
      control: 'text',
      description: 'Etiketten til valglisten.',
    },
    reverse: {
      control: 'boolean',
      description: 'Viser feltene i omvendt rekkefølge.',
    },
    disabled: {
      control: 'boolean',
      description: 'Deaktiverer skjemafeltene.',
    },
  },
} satisfies Meta<typeof FormExample>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {};
