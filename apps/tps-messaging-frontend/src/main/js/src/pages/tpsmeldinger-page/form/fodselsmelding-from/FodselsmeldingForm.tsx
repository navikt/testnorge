import React, { useState } from 'react';

import { DatePickerFormItem, InputFormItem, Line, SelectFormItem } from '@navikt/dolly-komponenter';
import { sendFodselsmelding } from '@/service/SendTpsMeldingService';
import { format } from 'date-fns';
import { Alert } from '@navikt/ds-react';
import { EndringsmeldingForm } from '@/pages/tpsmeldinger-page/form/endringsmelding-form/EndringsmeldingForm';
import { Handling } from '@/pages/tpsmeldinger-page/form/dodsmelding-form/DodsmeldingForm';

const notEmptyString = (value: string) => !!value && value !== '';
const notEmptyList = (value: unknown[]) => !!value && value.length > 0;

export const FodselsmeldingForm = () => {
  const [miljoOptions, setMiljoOptions] = useState<string[]>([]);
  const [kjoennType, setKjoennType] = useState<string>('GUTT');
  const [identType, setIdentType] = useState<string>('FNR');
  const [farsIdent, setFarsIdent] = useState<string>('');
  const [morsIdent, setMorsIdent] = useState<string>('');
  const [foedselsdato, setFoedselsdato] = useState<string>(format(new Date(), 'y-MM-dd'));
  const [address, setAddress] = useState<string>('LAG_NY_ADRESSE');
  const [miljoer, setMiljoer] = useState<string[]>([]);
  const [validate, setValidate] = useState<boolean>(false);
  const [error, setError] = useState<string>('');

  const onValidate = () => {
    setValidate(true);
    return notEmptyString(foedselsdato) && notEmptyList(miljoer);
  };

  const onSend = (handling: Handling) =>
    sendFodselsmelding(
      {
        adresseFra: address,
        identFar: farsIdent !== '' ? farsIdent.trim() : null,
        identMor: morsIdent.trim(),
        identtype: identType,
        foedselsdato: foedselsdato,
        kjoenn: kjoennType,
      },
      miljoer,
    ).then((response) => {
      setError(response?.error);
      return Promise.resolve(response);
    });

  const getSuccessMessage = (value: string | null, handling?: Handling) =>
    `Gratulerer, person med ident ${value} ble født i miljø ${miljoer.join(', ')}.`;

  return (
    <EndringsmeldingForm
      labels={{
        submit: 'Opprett fødselsmelding',
        search: 'Mors ident',
      }}
      onSend={onSend}
      valid={onValidate}
      setIdent={(ident) => {
        setError('');
        setMiljoer([]);
        setMiljoOptions([]);
        setMorsIdent(ident?.trim());
      }}
      getSuccessMessage={getSuccessMessage}
      setMiljoer={(miljoer) => {
        setMiljoOptions(miljoer);

        if (miljoer?.length > 0) {
          setMiljoer([miljoer[0]]);
        }
      }}
    >
      <Line>
        <InputFormItem
          label="Fars ident"
          defaultValue=""
          onBlur={(e) => {
            setError(null);
            setFarsIdent(e.target.value);
          }}
        />
        <SelectFormItem
          label="Barnets identtype"
          htmlId="barnets-identtype-select"
          onChange={(value) => setIdentType(value && value.length > 0 ? value[0] : '')}
          options={[
            {
              value: 'FNR',
              label: 'FNR',
            },
            {
              value: 'DNR',
              label: 'DNR',
            },
            {
              value: 'BOST',
              label: 'BOST',
            },
          ]}
        />
      </Line>
      <Line>
        <SelectFormItem
          label="Barnets kjønn"
          htmlId="barnets-kjoen-select"
          onChange={(value) => setKjoennType(value && value.length > 0 ? value[0] : 'GUTT')}
          options={[
            {
              value: 'GUTT',
              label: 'Gutt',
            },
            {
              value: 'JENTE',
              label: 'Jente',
            },
            {
              value: 'UKJENT',
              label: 'Ukjent',
            },
          ]}
        />
        <SelectFormItem
          onChange={(value) => {
            setMiljoer([value]);
          }}
          htmlId="miljo-select"
          label="Send til miljo*"
          error={validate && !notEmptyList(miljoer) ? 'Påkrevd' : null}
          options={
            !miljoOptions || miljoOptions?.length === 0
              ? []
              : miljoOptions?.map((value: string) => ({
                  value: value,
                  label: value.toUpperCase(),
                }))
          }
        />
        <SelectFormItem
          label="Adresse"
          htmlId="adresse-select"
          onChange={(value) => setAddress(value && value.length > 0 ? value[0] : '')}
          options={[
            {
              value: 'LAG_NY_ADRESSE',
              label: 'Lag ny adresse',
            },
            {
              value: 'ARV_FRA_MORS',
              label: 'Arv fra mors',
            },
            {
              value: 'ARV_FRA_FARS',
              label: 'Arv fra fars',
            },
          ]}
        />
      </Line>
      <Line>
        <DatePickerFormItem
          id="foedselsdato-field"
          label="Barnets fødselesdato*"
          onBlur={(value) => setFoedselsdato(value)}
          required={true}
        />
      </Line>
      {notEmptyString(error) && (
        <div style={{ marginTop: '20px' }}>
          <Alert variant={'error'} closeButton onClose={() => setError('')}>
            {error}
          </Alert>
        </div>
      )}
    </EndringsmeldingForm>
  );
};
