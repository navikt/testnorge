import React from 'react';
import { Page } from '@navikt/dolly-komponenter';
import { useTpsMessagingXml } from '../../hooks/useTpsMessaging';
import { Controller, useForm } from 'react-hook-form';
import { sendTpsMelding } from '../../service/SendTpsMeldingService';
import { Button, Textarea, UNSAFE_Combobox, VStack } from '@navikt/ds-react';


const onValidSubmit = async (values: any) => {
  await sendTpsMelding(values.queue, values.message);
};

export const TpsMeldingerPage = () => {
  const {
    handleSubmit,
    control,
    formState: { errors }
  } = useForm({
    shouldFocusError: true,
    defaultValues: {
      queue: '',
      melding: ''
    }
  });
  const { queues, loading, error } = useTpsMessagingXml();

  if (loading) return <p>Henter køer...</p>;

  if (error) return <p>Noe gikk galt, kontakt team Dolly...</p>;

  return (
    <Page>
      <VStack as="form" gap="8" onSubmit={handleSubmit(onValidSubmit)}>
        <Controller
          control={control}
          rules={{ required: 'Du må velge minst en kø.' }}
          name="queue"
          render={({ field }) => (
            <UNSAFE_Combobox
              id={'kø'}
              label="Meldingskø"
              {...field}
              error={errors.queue?.message}
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
            <Textarea label={'XML melding'}
                      {...field}
                      error={errors.melding?.message} />
          )}
        />
        <div>
          <Button type="submit">Send inn</Button>
        </div>
      </VStack>
    </Page>
  );
};
