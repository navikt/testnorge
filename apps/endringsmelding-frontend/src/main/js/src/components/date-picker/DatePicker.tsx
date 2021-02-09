import React, { useState } from 'react';
import { Label } from 'nav-frontend-skjema';
import { Datepicker } from 'nav-datovelger';

type Props = {
  id: string;
  label: string;
  onBlur: (value: string) => void;
};

export default ({ id, label, onBlur }: Props) => {
  const [date, setDate] = useState('');
  return (
    <div onBlur={() => onBlur(date)}>
      <Label htmlFor={id}>{label}</Label>
      <Datepicker inputId={id} onChange={setDate} value={date} />
      <div role="alert" aria-live="assertive">
        <div className="skjemaelement__feilmelding">dummy</div>
      </div>
    </div>
  );
};
