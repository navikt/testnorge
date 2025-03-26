import React from 'react';
import { Page } from '@navikt/dolly-komponenter';
import { useTpsMessagingXml } from '../../hooks/useTpsMessaging';
import { Controller, useForm } from 'react-hook-form';
import { sendTpsMelding } from '../../service/SendTpsMeldingService';
import { Alert, Button, Textarea, UNSAFE_Combobox, VStack } from '@navikt/ds-react';
import { XMLValidator } from 'fast-xml-parser';
import PrettyCode from '../../components/PrettyCode';

export const TpsMeldingerPage = () => {
  const onValidSubmit = (values: any) => {
    sendTpsMelding(values.queue, values.melding).then((response) => {
      setSuccessMessage(response);
    });
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

  if (formErrors) {
    console.warn(formErrors);
  }

  if (loading) return <p>Henter køer...</p>;

  if (error) return <p>Noe gikk galt, kontakt team Dolly...</p>;

  return (
    <Page>
      <VStack as="form" gap="4" onSubmit={handleSubmit(onValidSubmit)}>
        <Controller
          control={control}
          rules={{ required: 'Du må velge en kø.' }}
          name="queue"
          render={({ field }) => (
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
          <Button type="submit">Send inn</Button>
        </div>
        {successMessage && (
          <Alert closeButton={true} variant={'success'}>
            {XMLValidator.validate(successMessage) ? (
              <PrettyCode codeString={successMessage} language="xml" />
            ) : (
              successMessage
            )}
          </Alert>
        )}
      </VStack>
    </Page>
  );
};
