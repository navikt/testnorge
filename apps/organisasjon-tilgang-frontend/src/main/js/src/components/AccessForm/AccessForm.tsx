import React, { useState } from 'react';
import { Input } from 'nav-frontend-skjema';
import { TimePicker } from '@/components/TimePicker';
import { SubmitForm } from '@/components/SubmitForm';
import { OrganisasjonTilgangService } from '@/services';

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
      <Input label="Organisasjonsnummer" onBlur={(event) => setOrgnummer(event.target.value)} />
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
