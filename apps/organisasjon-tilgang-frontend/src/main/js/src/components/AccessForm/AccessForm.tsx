import React, { useState } from 'react';
import { SubmitForm } from '@/components/SubmitForm';
import { OrganisasjonTilgangService } from '@/services';
import { InputFormItem, SelectFormItem, TimePicker } from '@navikt/dolly-komponenter/lib';
import styled from 'styled-components';

const StyledTimepicker = styled(TimePicker)`
  && {
    width: 170px;
  }
`;

const StyledSelectFormItem = styled(SelectFormItem)`
  && {
    width: 170px;
    margin: 10px 0;
    padding-right: 0;
  }
`;

const createDefaultDate = () => {
  const currentDate = new Date();
  return new Date(
    currentDate.getFullYear() + 3,
    currentDate.getDate(),
    currentDate.getDay(),
    currentDate.getHours()
  );
};

const miljoeOptions = [
  { value: 'q1', label: 'Q1' },
  { value: 'q2', label: 'Q2' },
];

const AccessForm = () => {
  const [gyldigTil, setGyldigTil] = useState<Date>(createDefaultDate());
  const [orgnummer, setOrgnummer] = useState<string>('');
  const [miljoe, setMiljoe] = useState<string>('q1');

  return (
    <SubmitForm
      onSubmit={() =>
        OrganisasjonTilgangService.createOrganisasjonTilgang(
          orgnummer,
          gyldigTil.toISOString(),
          miljoe
        )
      }
    >
      <h2>Opprett tilgang</h2>
      <InputFormItem
        label="Organisasjonsnummer"
        onBlur={(event) => setOrgnummer(event.target.value)}
      />
      {/* @ts-ignore */}
      <StyledSelectFormItem
        options={miljoeOptions}
        label="Miljø"
        onChange={(value) => setMiljoe(value[0])}
      />
      {/* @ts-ignore */}
      <StyledTimepicker
        value={gyldigTil}
        label="Gyldig til dato"
        onChange={(value: Date) => {
          setGyldigTil(value);
        }}
      />
    </SubmitForm>
  );
};

AccessForm.displayName = 'AccessFrom';

export default AccessForm;
