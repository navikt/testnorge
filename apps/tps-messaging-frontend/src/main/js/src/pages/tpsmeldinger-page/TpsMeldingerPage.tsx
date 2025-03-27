import React from 'react';
import { Page } from '@navikt/dolly-komponenter';
import { useTpsMessagingXml } from '../../hooks/useTpsMessaging';
import { Controller, useForm } from 'react-hook-form';
import { sendTpsMelding } from '../../service/SendTpsMeldingService';
import { Alert, Button, CopyButton, Textarea, UNSAFE_Combobox, VStack } from '@navikt/ds-react';
import { XMLValidator } from 'fast-xml-parser';
import PrettyCode from '../../components/PrettyCode';
import AlertWithCloseButton from '../../components/AlertWithCloseButton';

export const TpsMeldingerPage = () => {
  const onValidSubmit = (values: any) => {
    setIsSending(true);
    setSuccessMessage('');
    setErrorResponse('');
    sendTpsMelding(values.queue, values.melding)
      .then((response) => {
        setSuccessMessage(response);
      })
      .catch((error: Error) => {
        setErrorResponse(error?.message);
      })
      .finally(() => setIsSending(false));
  };

  const {
    handleSubmit,
    control,
    formState: { errors: formErrors },
  } = useForm({
    shouldFocusError: true,
    defaultValues: {
      queue: '',
      melding: '',
    },
  });
  const { queues, loading, error } = useTpsMessagingXml();
  const [successMessage, setSuccessMessage] = React.useState('');
  const [isSending, setIsSending] = React.useState(false);
  const [errorResponse, setErrorResponse] = React.useState('');

  if (formErrors) {
    console.warn(formErrors);
  }

  if (loading) return <p>Henter køer...</p>;

  if (error)
    return (
      <Alert variant={'error'}>
        Noe gikk galt... Ta kontakt med team{' '}
        <a href="https://nav-it.slack.com/archives/CA3P9NGA2">#dolly</a> på Slack eller på epost
        dolly@nav.no.
      </Alert>
    );

  return (
    <Page>
      <VStack as="form" gap="4" onSubmit={handleSubmit(onValidSubmit)}>
        <Controller
          control={control}
          rules={{ required: 'Du må velge en kø.' }}
          name="queue"
          render={({ field }) => (
            <div style={{ display: 'flex', flexDirection: 'row', gap: '1rem' }}>
              <CopyButton copyText={field.value} />
              <UNSAFE_Combobox
                id={'queue'}
                label="Meldingskø"
                name={field.name}
                ref={field.ref}
                onToggleSelected={(option, isSelected) => {
                  if (isSelected) {
                    field.onChange(option);
                  }
                }}
                error={formErrors.queue?.message}
                options={queues || []}
                allowNewValues
                shouldAutocomplete
              />
            </div>
          )}
        />
        <Controller
          control={control}
          rules={{
            required: 'Melding kan ikke være tom',
            validate: {
              xmlFormat: (value, formValues) => {
                if (!value || !formValues.queue?.includes('xml')) {
                  return true;
                }
                const result = XMLValidator.validate(value);
                return (
                  result === true ||
                  'Ugyldig XML-format. Kontroller at XML-koden er korrekt formatert, eller velg en kø som ikke krever XML.'
                );
              },
            },
          }}
          name="melding"
          render={({ field }) => (
            <Textarea
              label={'TPS melding'}
              id={'melding'}
              {...field}
              error={formErrors.melding?.message}
            />
          )}
        />
        <div>
          <Button type="submit" loading={isSending} disabled={isSending}>
            Send inn
          </Button>
        </div>
        {successMessage && (
          <AlertWithCloseButton variant={'success'}>
            {XMLValidator.validate(successMessage) ? (
              <PrettyCode codeString={successMessage} language="xml" />
            ) : (
              successMessage
            )}
          </AlertWithCloseButton>
        )}
        {errorResponse && (
          <AlertWithCloseButton variant={'error'}>{errorResponse}</AlertWithCloseButton>
        )}
      </VStack>
    </Page>
  );
};
