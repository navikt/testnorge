import React, { useState } from 'react';
import { Label } from 'nav-frontend-skjema';
import { Datepicker } from 'nav-datovelger';

type Props = {
  id: string;
  label: string;
  onBlur: (value: string) => void;
  error?: string;
};

export default ({ id, label, onBlur, error, ...props }: Props) => {
  const [date, setDate] = useState('');

  return (
    <div onBlur={() => onBlur(date)} {...props}>
      <Label htmlFor={id}>{label}</Label>
      {/*  TODO fiks i designbiblioteket*/}
      <div
        className={error ? 'skjemaelement__input--harFeil' : ''}
        style={{ borderRadius: 4, display: 'inline-block' }}
      >
        <Datepicker inputId={id} onChange={setDate} value={date} />
      </div>
      {error && (
        <div role="alert" aria-live="assertive" className="skjemaelement__feilmelding">
          <p className="typo-feilmelding">{error}</p>
        </div>
      )}
    </div>
  );
};
