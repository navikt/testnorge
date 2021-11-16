import React, { useState } from 'react';
import { Input } from 'nav-frontend-skjema';
import { TimePicker } from '@/components/TimePicker';
import { Hovedknapp } from 'nav-frontend-knapper';
import styled from 'styled-components';

const StyledFrom = styled.form`
  display: flex;
  flex-direction: column;
  max-width: 300px;
`;

const StyledHovedknapp = styled(Hovedknapp)`
  margin: 20px 0;
`;

const AccessFrom = () => {
  const [gydligTil, setGydligTil] = useState<Date>();

  console.log(gydligTil && gydligTil.toISOString());
  return (
    <StyledFrom>
      <Input label="Organisasjonsnummer" />
      {/* @ts-ignore */}
      <TimePicker
        value={gydligTil}
        label="Gyldig Til"
        onChange={(value: Date) => {
          console.log(value);
          setGydligTil(value);
        }}
      />
      <StyledHovedknapp>Send</StyledHovedknapp>
    </StyledFrom>
  );
};

AccessFrom.displayName = 'AccessFrom';

export default AccessFrom;
