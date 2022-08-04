import React, { useState } from 'react';
import { DayPicker } from 'react-day-picker';
import nb from 'date-fns/locale/nb';

type Props = {
  id: string;
  required?: boolean;
  label: string;
  onBlur: (value: string) => void;
  error?: string;
};

export default ({ id, label, onBlur, required = false, error, ...props }: Props) => {
  const [date, setDate] = useState<Date>();

  return (
    <DayPicker
      key={id}
      mode={'single'}
      onSelect={setDate}
      selected={date}
      footer={label}
      locale={nb}
      required={required}
      {...props}
    />
  );
};
