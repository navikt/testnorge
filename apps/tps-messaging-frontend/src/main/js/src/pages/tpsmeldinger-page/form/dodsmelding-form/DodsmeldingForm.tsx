import React, { useState } from 'react';

import { DatePickerFormItem, Line, SelectFormItem } from '@navikt/dolly-komponenter';
import { sendDodsmelding, slettDodsmelding } from '@/service/SendTpsMeldingService';
import { format } from 'date-fns';
import { Alert } from '@navikt/ds-react';
import { EndringsmeldingForm } from '@/pages/tpsmeldinger-page/form/endringsmelding-form/EndringsmeldingForm';

export type Handling = 'SETTE_DOEDSDATO' | 'ENDRET_DOEDSDATO' | 'ANNULLERE_DOEDSDATO';

const notEmptyString = (value: string) => !!value && value !== '';
const notEmptyList = (value: unknown[]) => !!value && value.length > 0;

export const DodsmeldingForm = () => {
  const [miljoOptions, setMiljoOptions] = useState<string[]>([]);
  const [ident, setIdent] = useState<string>('');
  const [doedsdato, setDoedsdato] = useState<string>(format(new Date(), 'y-MM-dd'));
  const [valgteMiljoer, setValgteMiljoer] = useState<string[]>([]);
  const [validate, setValidate] = useState<boolean>(false);
  const [error, setError] = useState<string>('');

  const onValidate = (handling: Handling) => {
    setValidate(true);
    return (
      (handling === 'ANNULLERE_DOEDSDATO' || notEmptyString(doedsdato)) &&
      notEmptyList(valgteMiljoer)
    );
  };

  const onSend = (handling: Handling): Promise<any> => {
    if (handling === 'ANNULLERE_DOEDSDATO') {
      return slettDodsmelding(ident.trim(), valgteMiljoer).then((response) => {
        setError(response?.error);
        return Promise.resolve(response);
      });
    }
    return sendDodsmelding(
      {
        doedsdato: doedsdato,
        ident: ident.trim(),
        handling: handling,
      },
      valgteMiljoer,
    ).then((response) => {
      setError(response?.error);
      return Promise.resolve(response);
    });
  };

  const getSuccessMessage = (value: string, handling?: Handling) => {
    if (handling === 'ANNULLERE_DOEDSDATO') {
      return `Dødsmelding annulert for ident ${value} i miljø ${valgteMiljoer}.`;
    }
    return `Dødsmelding for ident ${value} ble sendt til miljø ${valgteMiljoer}.`;
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
        setError(null);
        setMiljoOptions([]);
        setValgteMiljoer([]);
        setIdent(ident);
      }}
      getSuccessMessage={getSuccessMessage}
      setMiljoer={(miljoer) => {
        setMiljoOptions(miljoer);
        if (miljoer?.length > 0) {
          setValgteMiljoer([miljoer[0]]);
        }
      }}
    >
      <Line>
        <DatePickerFormItem
          id="doedsdato-field"
          label="Dødsdato*"
          onBlur={(value: string) => setDoedsdato(value)}
          error={validate && !notEmptyString(doedsdato) ? 'Påkrevd' : null}
        />
        <SelectFormItem
          onChange={(value: string[]) => setValgteMiljoer(value)}
          htmlId="miljo-dodsdato-select"
          multi={true}
          label="Send til miljo*"
          error={validate && !notEmptyList(valgteMiljoer) ? 'Påkrevd' : null}
          options={
            !miljoOptions || miljoOptions?.length === 0
              ? []
              : miljoOptions?.map((value: string) => ({
                  value: value,
                  label: value.toUpperCase(),
                }))
          }
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
