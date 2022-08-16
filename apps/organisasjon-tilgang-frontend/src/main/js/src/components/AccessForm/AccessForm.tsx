import React, { useState } from 'react';
import { SubmitForm } from '@/components/SubmitForm';
import { OrganisasjonTilgangService } from '@/services';
import { InputFormItem, TimePicker } from '@navikt/dolly-komponenter/lib';

const createDefaultDate = () => {
  const currentDate = new Date();
  return new Date(
    currentDate.getFullYear() + 3,
    currentDate.getDate(),
    currentDate.getDay(),
    currentDate.getHours()
  );
};

const AccessForm = () => {
  const [gyldigTil, setGyldigTil] = useState<Date>(createDefaultDate());
  const [orgnummer, setOrgnummer] = useState<string>('');
  return (
    <SubmitForm
      onSubmit={() =>
        OrganisasjonTilgangService.createOrganisasjonTilgang(orgnummer, gyldigTil.toISOString())
      }
    >
      <h2>Opprett tilgang</h2>
      <InputFormItem
        label="Organisasjonsnummer"
        onBlur={(event) => setOrgnummer(event.target.value)}
      />
      {/* @ts-ignore */}
      <TimePicker
        value={gyldigTil}
        label="Gyldig Til"
        onChange={(value: Date) => {
          setGyldigTil(value);
        }}
      />
    </SubmitForm>
  );
};

AccessForm.displayName = 'AccessFrom';

export default AccessForm;
