import React from 'react';
import { Page } from '@navikt/dolly-komponenter';
import { useTpsMessagingXml } from '../../hooks/useTpsMessaging';
import { Controller, useForm } from 'react-hook-form';
import { sendTpsMelding } from '../../service/SendTpsMeldingService';
import { Alert, Button, Textarea, UNSAFE_Combobox, VStack } from '@navikt/ds-react';

export const TpsMeldingerPage = () => {
  const onValidSubmit = (values: any) => {
    sendTpsMelding(values.queue, values.melding).then((response) => {
      setSuccessMessage(response);
      reset();
    });
  };

  const {
    reset,
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
          rules={{ required: 'Du må velge minst en kø.' }}
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
              options={queues}
              shouldAutocomplete
            />
          )}
        />
        <Controller
          control={control}
          rules={{ required: 'XML melding kan ikke være tom' }}
          name="melding"
          render={({ field }) => (
            <Textarea
              label={'XML melding'}
              id={'melding'}
              {...field}
              error={formErrors.melding?.message}
            />
          )}
        />
        <div>
          <Button type="submit">Send inn</Button>
        </div>
        {successMessage && <Alert variant={'success'}>{successMessage}</Alert>}
      </VStack>
    </Page>
  );
};
