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
  const [gydligTil, setGydligTil] = useState<Date>(createDefaultDate());
  const [orgnummer, setOrgnummer] = useState<string>('');
  return (
    <SubmitForm
      onSubmit={() =>
        OrganisasjonTilgangService.createOrganisasjonTilgang(orgnummer, gydligTil.toISOString())
      }
    >
      <h2>Opprett tilgang</h2>
      <Input label="Organisasjonsnummer" onBlur={(event) => setOrgnummer(event.target.value)} />
      {/* @ts-ignore */}
      <TimePicker
        value={gydligTil}
        label="Gyldig Til"
        onChange={(value: Date) => {
          console.log(value);
          setGydligTil(value);
        }}
      />
    </SubmitForm>
  );
};

AccessForm.displayName = 'AccessFrom';

export default AccessForm;
