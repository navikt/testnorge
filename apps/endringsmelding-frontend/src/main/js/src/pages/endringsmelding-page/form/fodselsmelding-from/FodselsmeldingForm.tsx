import React, { useState } from 'react';
import { LocalAlert, Select, TextField } from '@navikt/ds-react';
import { sendFodselsmelding } from '@/service/EndringsmeldingService';
import { format } from 'date-fns';
import { ControlledDatePickerField } from '@/components/form/ControlledDatePickerField';
import { EndringsmeldingForm } from '@/pages/endringsmelding-page/form/endringsmelding-form/EndringsmeldingForm';
import type { Handling } from '@/pages/endringsmelding-page/form/dodsmelding-form/DodsmeldingForm';
import {
  AlertOffset,
  FormRow,
  HalfField,
  QuarterField,
} from '@/pages/endringsmelding-page/form/FormLayout';

const notEmptyString = (value: string) => !!value && value !== '';
const notEmptyList = (value: unknown[]) => !!value && value.length > 0;
type SubmitHandling = Handling | null;
type Kjoenn = 'GUTT' | 'JENTE' | 'UKJENT';
type AdresseFra = 'LAG_NY_ADRESSE' | 'ARV_FRA_MORS' | 'ARV_FRA_FARS';
const isKjoenn = (value: string): value is Kjoenn =>
  value === 'GUTT' || value === 'JENTE' || value === 'UKJENT';
const isAdresseFra = (value: string): value is AdresseFra =>
  value === 'LAG_NY_ADRESSE' || value === 'ARV_FRA_MORS' || value === 'ARV_FRA_FARS';

export const FodselsmeldingForm = () => {
  const [miljoOptions, setMiljoOptions] = useState<string[]>([]);
  const [kjoennType, setKjoennType] = useState<Kjoenn>('GUTT');
  const [identType, setIdentType] = useState('FNR');
  const [farsIdent, setFarsIdent] = useState<string>('');
  const [morsIdent, setMorsIdent] = useState<string>('');
  const [foedselsdato, setFoedselsdato] = useState<string>(format(new Date(), 'yyyy-MM-dd'));
  const [address, setAddress] = useState<AdresseFra>('LAG_NY_ADRESSE');
  const [miljoer, setMiljoer] = useState<string[]>([]);
  const [validate, setValidate] = useState<boolean>(false);
  const [error, setError] = useState<string>('');

  const onValidate = () => {
    setValidate(true);
    return notEmptyString(foedselsdato) && notEmptyList(miljoer);
  };

  const onSend = (_handling: SubmitHandling) =>
    sendFodselsmelding(
      {
        adresseFra: address,
        identFar: farsIdent.trim() || undefined,
        identMor: morsIdent.trim(),
        identtype: identType,
        foedselsdato: foedselsdato,
        kjoenn: kjoennType,
      },
      miljoer,
    ).then((response) => {
      setError(response?.error || '');
      return Promise.resolve(response);
    });

  const getSuccessMessage = (value: string | undefined, _handling?: SubmitHandling) =>
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
        setValidate(false);
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
      <FormRow>
        <HalfField>
          <TextField
            label="Fars ident"
            autoComplete="off"
            inputMode="numeric"
            value={farsIdent}
            onChange={(event) => {
              setError('');
              setFarsIdent(event.target.value);
            }}
          />
        </HalfField>
        <QuarterField>
          <Select
            label="Barnets identtype"
            id="barnets-identtype-select"
            value={identType}
            onChange={(event) => setIdentType(event.target.value)}
          >
            <option value="FNR">FNR</option>
            <option value="DNR">DNR</option>
            <option value="BOST">BOST</option>
          </Select>
        </QuarterField>
      </FormRow>
      <FormRow>
        <QuarterField>
          <Select
            label="Barnets kjønn"
            id="barnets-kjoen-select"
            value={kjoennType}
            onChange={(event) => {
              const { value } = event.target;
              if (isKjoenn(value)) {
                setKjoennType(value);
              }
            }}
          >
            <option value="GUTT">Gutt</option>
            <option value="JENTE">Jente</option>
            <option value="UKJENT">Ukjent</option>
          </Select>
        </QuarterField>
        <QuarterField>
          <Select
            id="miljo-select"
            label="Send til miljø*"
            value={miljoer[0] || ''}
            error={validate && !notEmptyList(miljoer) ? 'Påkrevd' : undefined}
            onChange={(event) => setMiljoer(event.target.value ? [event.target.value] : [])}
          >
            {miljoOptions.map((value) => (
              <option key={value} value={value}>
                {value.toUpperCase()}
              </option>
            ))}
          </Select>
        </QuarterField>
        <QuarterField>
          <Select
            label="Adresse"
            id="adresse-select"
            value={address}
            onChange={(event) => {
              const { value } = event.target;
              if (isAdresseFra(value)) {
                setAddress(value);
              }
            }}
          >
            <option value="LAG_NY_ADRESSE">Lag ny adresse</option>
            <option value="ARV_FRA_MORS">Arv fra mors</option>
            <option value="ARV_FRA_FARS">Arv fra fars</option>
          </Select>
        </QuarterField>
      </FormRow>
      <FormRow>
        <HalfField>
          <ControlledDatePickerField
            id="foedselsdato-field"
            label="Barnets fødselsdato*"
            value={foedselsdato}
            onChange={setFoedselsdato}
            required
            error={validate && !notEmptyString(foedselsdato) ? 'Påkrevd' : undefined}
          />
        </HalfField>
      </FormRow>
      {notEmptyString(error) && (
        <AlertOffset>
          <LocalAlert status="error" size="small">
            <LocalAlert.Header>
              <LocalAlert.Title as="div">Kunne ikke sende fødselsmeldingen</LocalAlert.Title>
              <LocalAlert.CloseButton onClick={() => setError('')} />
            </LocalAlert.Header>
            <LocalAlert.Content>{error}</LocalAlert.Content>
          </LocalAlert>
        </AlertOffset>
      )}
    </EndringsmeldingForm>
  );
};
