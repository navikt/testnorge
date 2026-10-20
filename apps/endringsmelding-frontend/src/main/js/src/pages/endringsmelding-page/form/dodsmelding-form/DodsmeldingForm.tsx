import React, { useState } from 'react';
import { LocalAlert, UNSAFE_Combobox } from '@navikt/ds-react';
import { sendDodsmelding, slettDodsmelding } from '@/service/EndringsmeldingService';
import { format } from 'date-fns';
import { ControlledDatePickerField } from '@/components/form/ControlledDatePickerField';
import { EndringsmeldingForm } from '@/pages/endringsmelding-page/form/endringsmelding-form/EndringsmeldingForm';
import {
  AlertOffset,
  FormRow,
  HalfField,
  QuarterField,
} from '@/pages/endringsmelding-page/form/FormLayout';

export type Handling = 'SETTE_DOEDSDATO' | 'ENDRET_DOEDSDATO' | 'ANNULLERE_DOEDSDATO';
type SubmitHandling = Handling | null;

const notEmptyString = (value: string) => !!value && value !== '';
const notEmptyList = (value: unknown[]) => !!value && value.length > 0;

export const DodsmeldingForm = () => {
  const [miljoOptions, setMiljoOptions] = useState<string[]>([]);
  const [ident, setIdent] = useState<string>('');
  const [doedsdato, setDoedsdato] = useState<string>(format(new Date(), 'yyyy-MM-dd'));
  const [valgteMiljoer, setValgteMiljoer] = useState<string[]>([]);
  const [miljoInputValue, setMiljoInputValue] = useState('');
  const [validate, setValidate] = useState<boolean>(false);
  const [error, setError] = useState<string>('');

  const onValidate = (handling: SubmitHandling) => {
    setValidate(true);
    return (
      (handling === 'ANNULLERE_DOEDSDATO' || notEmptyString(doedsdato)) &&
      notEmptyList(valgteMiljoer)
    );
  };

  const onSend = (handling: SubmitHandling): Promise<any> => {
    const resolvedHandling = handling ?? 'SETTE_DOEDSDATO';

    if (resolvedHandling === 'ANNULLERE_DOEDSDATO') {
      return slettDodsmelding(ident.trim(), valgteMiljoer).then((response) => {
        setError(response?.error || '');
        return Promise.resolve(response);
      });
    }
    return sendDodsmelding(
      {
        doedsdato: doedsdato,
        ident: ident.trim(),
        handling: resolvedHandling,
      },
      valgteMiljoer,
    ).then((response) => {
      setError(response?.error || '');
      return Promise.resolve(response);
    });
  };

  const getSuccessMessage = (value: string | undefined, handling?: SubmitHandling) => {
    if (handling === 'ANNULLERE_DOEDSDATO') {
      return `Dødsmelding annulert for ident ${value} i miljø ${valgteMiljoer.join(', ')}.`;
    }
    return `Dødsmelding for ident ${value} ble sendt til miljø ${valgteMiljoer.join(', ')}.`;
  };
  return (
    <EndringsmeldingForm
      labels={{
        submit: 'Opprett dødsmelding',
        search: 'Ident',
        delete: 'Annuller dødsmelding',
      }}
      onSend={onSend}
      valid={onValidate}
      setIdent={(ident) => {
        setError('');
        setMiljoOptions([]);
        setValgteMiljoer([]);
        setMiljoInputValue('');
        setValidate(false);
        setIdent(ident);
      }}
      getSuccessMessage={getSuccessMessage}
      setMiljoer={(miljoer) => {
        setMiljoOptions(miljoer);
        if (miljoer?.length > 0) {
          setValgteMiljoer([miljoer[0]]);
        }
        setMiljoInputValue('');
      }}
    >
      <FormRow>
        <HalfField>
          <ControlledDatePickerField
            id="doedsdato-field"
            label="Dødsdato*"
            value={doedsdato}
            onChange={setDoedsdato}
            required
            error={validate && !notEmptyString(doedsdato) ? 'Påkrevd' : undefined}
          />
        </HalfField>
        <QuarterField>
          <UNSAFE_Combobox
            label="Send til miljø*"
            options={miljoOptions.map((value) => ({ value, label: value.toUpperCase() }))}
            isMultiSelect
            shouldAutocomplete
            value={miljoInputValue}
            onChange={setMiljoInputValue}
            selectedOptions={valgteMiljoer}
            onToggleSelected={(option, isSelected) => {
              const nextValues = isSelected
                ? [...new Set([...valgteMiljoer, option])]
                : valgteMiljoer.filter((value) => value !== option);
              setValgteMiljoer(miljoOptions.filter((value) => nextValues.includes(value)));
              setMiljoInputValue('');
            }}
            error={validate && !notEmptyList(valgteMiljoer) ? 'Påkrevd' : undefined}
          />
        </QuarterField>
      </FormRow>
      {notEmptyString(error) && (
        <AlertOffset>
          <LocalAlert status="error" size="small">
            <LocalAlert.Header>
              <LocalAlert.Title as="div">Kunne ikke sende dødsmeldingen</LocalAlert.Title>
              <LocalAlert.CloseButton onClick={() => setError('')} />
            </LocalAlert.Header>
            <LocalAlert.Content>{error}</LocalAlert.Content>
          </LocalAlert>
        </AlertOffset>
      )}
    </EndringsmeldingForm>
  );
};
