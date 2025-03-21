import React, { useState } from 'react';

import {
  ErrorAlertstripe,
  Form,
  Knapp,
  Line,
  SuccessAlertstripe,
  WarningAlertstripe,
} from '@navikt/dolly-komponenter';
import { BadRequestError } from '@navikt/dolly-lib/lib/error';
import { Handling } from '@/pages/tpsmeldinger-page/form/dodsmelding-form/DodsmeldingForm';
import { Search } from '@/components/search/SearchDiv';

type Props<T> = {
  children: React.ReactNode;
  labels: {
    search: string;
    submit: string;
    delete?: string;
  };
  getSuccessMessage: (value?: string, handling?: Handling) => string;
  getErrorMessage?: () => string;
  onSend: (handling: Handling) => Promise<any>;
  valid: (handling: Handling) => boolean;
  setIdent: (value: string) => void;
  setMiljoer: (value: string[]) => void;
};

export const EndringsmeldingForm = <T extends {}>({
  children,
  onSend,
  valid,
  labels,
  setMiljoer,
  setIdent,
  getSuccessMessage,
  getErrorMessage = () => 'Noe gikk galt.',
}: Props<T>) => {
  const [loading, setLoading] = useState(null);
  const [show, setShow] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [warningMessages, setWarningMessages] = useState<string[]>([]);

  if (warningMessages && warningMessages.length > 0) {
    console.log(warningMessages);
  }

  const onSubmit = (event: React.MouseEvent<HTMLButtonElement>, handling: Handling) => {
    event.preventDefault();
    if (valid(handling)) {
      setLoading(handling || 'SETTE_DOEDSDATO');
      onSend(handling)
        .then((response) => {
          setLoading(null);
          if (!response?.error) {
            setSuccessMessage(getSuccessMessage(response?.ident, handling));
          }
        })
        .catch((e) => {
          setLoading(null);
          if (e instanceof BadRequestError) {
            return e.response.json().then((body: string[]) => setWarningMessages(body));
          }

          setErrorMessage(getErrorMessage());
        });
    }
  };
  return (
    <Form>
      <Search
        onChange={(value) => {
          setSuccessMessage('');
          setErrorMessage('');
          setWarningMessages([]);
          setIdent(value);
        }}
        setShow={setShow}
        setMiljoer={setMiljoer}
        labels={{
          label: labels.search,
          delete: 'Slett dødsmelding',
          button: 'Søk etter person',
          onFound: 'Person funnet',
          onNotFound: 'Person ikke funnet',
          onError: 'Noe gikk galt',
          syntIdent: 'Endringsmelding støtter ikke synt-identer.',
        }}
      />
      {show && (
        <>
          {children}
          <Line reverse={true}>
            <Knapp
              variant={'primary'}
              onClick={(event: React.MouseEvent<HTMLButtonElement, MouseEvent>) =>
                onSubmit(event, null)
              }
              disabled={loading}
              loading={loading === 'SETTE_DOEDSDATO'}
            >
              {labels.submit}
            </Knapp>
            {labels.delete && (
              <Knapp
                variant={'danger'}
                onClick={(event: React.MouseEvent<HTMLButtonElement, MouseEvent>) =>
                  onSubmit(event, 'ANNULLERE_DOEDSDATO')
                }
                disabled={loading}
                loading={loading === 'ANNULLERE_DOEDSDATO'}
              >
                {labels.delete}
              </Knapp>
            )}
          </Line>
        </>
      )}
      {!!successMessage && <SuccessAlertstripe label={successMessage} />}
      {!!errorMessage && <ErrorAlertstripe label={errorMessage} />}
      {!!warningMessages &&
        warningMessages.map((warning, index) => <WarningAlertstripe key={index} label={warning} />)}
    </Form>
  );
};
