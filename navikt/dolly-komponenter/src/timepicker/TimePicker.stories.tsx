import React, { useEffect, useState } from 'react';
import type { Meta, StoryObj } from '@storybook/react-vite';
import { fn } from 'storybook/test';
import TimePicker from './TimePicker';

type ControlledTimePickerProps = Omit<
  React.ComponentProps<typeof TimePicker>,
  'value' | 'minDate' | 'maxDate'
> & {
  value: Date | number | null;
  minDate?: Date | number;
  maxDate?: Date | number;
};

const normalizeDate = (value: Date | number | null) =>
  typeof value === 'number' ? new Date(value) : value;

const normalizeOptionalDate = (value: Date | number | undefined) =>
  typeof value === 'number' ? new Date(value) : value;

const ControlledTimePicker = (props: ControlledTimePickerProps) => {
  const [value, setValue] = useState<Date | null>(() => normalizeDate(props.value));

  useEffect(() => {
    setValue(normalizeDate(props.value));
  }, [props.value]);

  const handleChange = (nextValue: Date | null) => {
    setValue(nextValue);
    props.onChange(nextValue);
  };

  return (
    <TimePicker
      {...props}
      value={value}
      minDate={normalizeOptionalDate(props.minDate)}
      maxDate={normalizeOptionalDate(props.maxDate)}
      onChange={handleChange}
    />
  );
};

const meta = {
  title: 'Form/TimePicker',
  component: TimePicker,
  render: (args) => <ControlledTimePicker {...args} />,
  args: {
    label: 'Dato og tidspunkt',
    name: 'story-time',
    value: new Date(2026, 7, 13, 10, 30),
    onChange: fn(),
  },
  parameters: {
    docs: {
      description: {
        component: 'Velger dato og klokkeslett i ett kontrollert skjemafelt.',
      },
    },
  },
  argTypes: {
    label: {
      control: 'text',
      description: 'Etiketten som beskriver feltet.',
    },
    name: {
      control: 'text',
      description: 'Feltets name- og id-attributt.',
    },
    value: {
      control: 'date',
      description: 'Valgt dato og klokkeslett.',
    },
    placeholder: {
      control: 'text',
      description: 'Tekst som vises når ingen verdi er valgt.',
    },
    disabled: {
      control: 'boolean',
      description: 'Angir om feltet er deaktivert.',
    },
    minDate: {
      control: 'date',
      description: 'Tidligste dato brukeren kan velge.',
    },
    maxDate: {
      control: 'date',
      description: 'Seneste dato brukeren kan velge.',
    },
    excludeDates: {
      control: 'object',
      description: 'Datoer som ikke skal kunne velges.',
    },
    onBlur: {
      control: false,
      description: 'Kalles når feltet mister fokus.',
    },
    onChange: {
      control: false,
      description: 'Kalles når valgt dato eller klokkeslett endres.',
    },
  },
} satisfies Meta<typeof TimePicker>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {};

export const Disabled: Story = {
  args: {
    disabled: true,
  },
};
