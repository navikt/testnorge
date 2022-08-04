import React, { useState } from 'react';
import { DayPicker } from 'react-day-picker';
import nb from 'date-fns/locale/nb';

type Props = {
  id: string;
  label: string;
  onBlur: (value: string) => void;
  error?: string;
};

export default ({ id, label, onBlur, error, ...props }: Props) => {
  const [date, setDate] = useState<Date>();

  return (
    <div onBlur={() => onBlur(date.toDateString())} {...props}>
      <DayPicker
        key={id}
        mode={'single'}
        onSelect={setDate}
        selected={date}
        footer={label}
        locale={nb}
      />
    </div>
  );
};
