import React, { useState } from 'react';
import { Button, LocalAlert } from '@navikt/ds-react';
import { BadRequestError } from '@navikt/dolly-lib/lib/error';
import type { Handling } from '@/pages/endringsmelding-page/form/dodsmelding-form/DodsmeldingForm';
import { Search } from '@/components/search/SearchDiv';
import { ActionRow, AlertStack, FormRoot } from '@/pages/endringsmelding-page/form/FormLayout';

type SubmitHandling = Handling | null;

type Props = {
  children: React.ReactNode;
  labels: {
    search: string;
    submit: string;
    delete?: string;
  };
  getSuccessMessage: (value?: string, handling?: SubmitHandling) => string;
  getErrorMessage?: () => string;
  onSend: (handling: SubmitHandling) => Promise<any>;
  valid: (handling: SubmitHandling) => boolean;
  setIdent: (value: string) => void;
  setMiljoer: (value: string[]) => void;
};

export const EndringsmeldingForm = ({
  children,
  onSend,
  valid,
  labels,
  setMiljoer,
  setIdent,
  getSuccessMessage,
  getErrorMessage = () => 'Noe gikk galt.',
}: Props) => {
  const [loading, setLoading] = useState<Handling | null>(null);
  const [show, setShow] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [warningMessages, setWarningMessages] = useState<string[]>([]);

  const onSubmit = (handling: SubmitHandling) => {
    setSuccessMessage('');
    setErrorMessage('');
    setWarningMessages([]);

    if (valid(handling)) {
      setLoading(handling ?? 'SETTE_DOEDSDATO');
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
    <FormRoot>
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
          <ActionRow>
            <Button
              type="button"
              variant="primary"
              onClick={() => onSubmit(null)}
              disabled={Boolean(loading)}
              loading={loading === 'SETTE_DOEDSDATO'}
            >
              {labels.submit}
            </Button>
            {labels.delete && (
              <Button
                type="button"
                variant="primary"
                data-color="danger"
                onClick={() => onSubmit('ANNULLERE_DOEDSDATO')}
                disabled={Boolean(loading)}
                loading={loading === 'ANNULLERE_DOEDSDATO'}
              >
                {labels.delete}
              </Button>
            )}
          </ActionRow>
        </>
      )}
      {(successMessage || errorMessage || warningMessages.length > 0) && (
        <AlertStack>
          {!!successMessage && (
            <LocalAlert status="success" size="small">
              <LocalAlert.Header>
                <LocalAlert.Title as="div">{successMessage}</LocalAlert.Title>
              </LocalAlert.Header>
            </LocalAlert>
          )}
          {!!errorMessage && (
            <LocalAlert status="error" size="small">
              <LocalAlert.Header>
                <LocalAlert.Title as="div">{errorMessage}</LocalAlert.Title>
              </LocalAlert.Header>
            </LocalAlert>
          )}
          {warningMessages.map((warning, index) => (
            <LocalAlert key={index} status="warning" size="small">
              <LocalAlert.Header>
                <LocalAlert.Title as="div">{warning}</LocalAlert.Title>
              </LocalAlert.Header>
            </LocalAlert>
          ))}
        </AlertStack>
      )}
    </FormRoot>
  );
};
