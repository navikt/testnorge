import React from 'react';
import { Page } from '@navikt/dolly-komponenter';
import { useTpsMessagingXml } from '../../hooks/useTpsMessaging';
import { Controller, useForm } from 'react-hook-form';
import { sendTpsMelding } from '../../service/SendTpsMeldingService';
import { Alert, Button, CopyButton, Textarea, UNSAFE_Combobox, VStack } from '@navikt/ds-react';
import { XMLValidator } from 'fast-xml-parser';
import PrettyCode from '../../components/PrettyCode';
import AlertWithCloseButton from '../../components/AlertWithCloseButton';
import './TpsMeldingerPage.less';

const xmlQueueDefaultValue =
  '<?xml version="1.0" encoding="ISO-8859-1"?>\n' +
  '<tpsPersonData>\n' +
  '<tpsServiceRutine>\n' +
  '<serviceRutinenavn>FS03-FDNUMMER-PERSDATA-O</serviceRutinenavn>\n' +
  '<fnr>12345678901</fnr>\n' +
  '<aksjonsDato></aksjonsDato>\n' +
  '<aksjonsKode>A</aksjonsKode>\n' +
  '<aksjonsKode2>0</aksjonsKode2>\n' +
  '</tpsServiceRutine>\n' +
  '</tpsPersonData>';

const infoQueueDefaultValue = 'FS03-FDNUMMER-PERSDATA-O;12345678901;;A;0';

export const TpsMeldingerPage = () => {
  const onValidSubmit = (values: any) => {
    setIsSending(true);
    resetResponse();
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
    setValue,
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

  const resetResponse = () => {
    setSuccessMessage('');
    setErrorResponse('');
  };

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
            <UNSAFE_Combobox
              id={'queue'}
              label="Meldingskø"
              name={field.name}
              ref={field.ref}
              onToggleSelected={(option, isSelected) => {
                if (isSelected) {
                  field.onChange(option);
                  setValue(
                    'melding',
                    option?.toUpperCase()?.includes('XML')
                      ? xmlQueueDefaultValue
                      : infoQueueDefaultValue,
                  );
                  resetResponse();
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
                if (
                  !value ||
                  (!formValues.queue?.includes('xml') && !formValues.queue?.includes('XML'))
                ) {
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
          <Button
            onClick={() => {
              resetResponse();
            }}
            type="submit"
            loading={isSending}
            disabled={isSending}
          >
            Send inn
          </Button>
        </div>
        {successMessage && (
          <div className={'navds-combobox__wrapper'} style={{ minWidth: '740px' }}>
            <CopyButton className={'copy-button'} copyText={successMessage} variant={'action'} />
            <AlertWithCloseButton onClose={resetResponse} variant={'success'}>
              {XMLValidator.validate(successMessage) ? (
                <PrettyCode codeString={successMessage} language="xml" />
              ) : (
                <p>{successMessage}</p>
              )}
            </AlertWithCloseButton>
          </div>
        )}
        {errorResponse && (
          <AlertWithCloseButton onClose={resetResponse} variant={'error'}>
            {errorResponse}
          </AlertWithCloseButton>
        )}
      </VStack>
    </Page>
  );
};
