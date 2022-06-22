import { Label } from '@navikt/ds-react';
import React, { useState } from 'react';
import { DayPicker } from 'react-day-picker';

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
      <Label>{label}</Label>
      {/*  TODO fiks i designbiblioteket*/}
      <div
        className={error ? 'skjemaelement__input--harFeil' : ''}
        style={{ borderRadius: 4, display: 'inline-block' }}
      >
        <DayPicker key={id} onSelect={setDate} value={date} />
      </div>
      {error && (
        <div role="alert" aria-live="assertive" className="skjemaelement__feilmelding">
          <p className="typo-feilmelding">{error}</p>
        </div>
      )}
    </div>
  );
};
