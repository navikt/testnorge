import React from 'react';
import { useParams } from 'react-router';
import { OrganisasjonFasteDataService, OrganisasjonService } from '@/service';
import { CompareCodeView } from '@/components/compare-code-view';
import { OrganisasjonComperator } from '@/comperator';
import {
  BodyLong,
  Heading,
  HStack,
  Loader,
  LocalAlert,
} from '@navikt/ds-react';
import { MismatchTable } from '@/components/mismatch-table';
import isNotFoundError from '@/isNotFoundError';
import { AppPage, ResultsStack, SectionStack } from '@/components/layout';

export default () => {
  const { orgnummer, miljo } = useParams() as { orgnummer: string; miljo: string };
  const [status, setStatus] = React.useState<'error' | 'loading' | 'not-found' | 'success'>(
    'loading'
  );
  const [data, setData] = React.useState<{
    left: Awaited<ReturnType<typeof OrganisasjonFasteDataService.fetchOrganisasjon>>;
    right: Awaited<ReturnType<typeof OrganisasjonService.fetchOrganisasjon>>;
  } | null>(null);

  React.useEffect(() => {
    let active = true;

    setStatus('loading');
    Promise.all([
      OrganisasjonFasteDataService.fetchOrganisasjon(orgnummer),
      OrganisasjonService.fetchOrganisasjon(orgnummer, miljo),
    ])
      .then((values) => {
        if (!active) {
          return;
        }
        setData({
          left: values[0],
          right: values[1],
        });
        setStatus('success');
      })
      .catch((error) => {
        if (!active) {
          return;
        }
        if (isNotFoundError(error)) {
          setStatus('not-found');
        } else {
          setStatus('error');
        }
      });

    return () => {
      active = false;
    };
  }, [miljo, orgnummer]);

  const compared = data ? OrganisasjonComperator.compare(data.left, data.right) : null;

  return (
    <AppPage>
      <SectionStack>
        <Heading level="1" size="large">
          Organisasjon differanse
        </Heading>
        {status === 'loading' && (
          <HStack align="center" gap="space-8">
            <Loader size="large" title="Laster differanse" />
            <BodyLong>Laster differanse...</BodyLong>
          </HStack>
        )}
        {status === 'not-found' && (
          <LocalAlert size="small" status="warning">
            <LocalAlert.Header>
              <LocalAlert.Title as="div">
                Datasett ikke funnet i miljø {miljo.toUpperCase()}.
              </LocalAlert.Title>
            </LocalAlert.Header>
          </LocalAlert>
        )}
        {status === 'error' && (
          <LocalAlert size="small" status="error">
            <LocalAlert.Header>
              <LocalAlert.Title as="div">Noe gikk galt under sammenligningen.</LocalAlert.Title>
            </LocalAlert.Header>
          </LocalAlert>
        )}
        {status === 'success' && data && compared && (
          <ResultsStack>
            {compared.isMismatch ? (
              <>
                <LocalAlert size="small" status="warning">
                  <LocalAlert.Header>
                    <LocalAlert.Title as="div">
                      Det er en mismatch mellom datasettene.
                    </LocalAlert.Title>
                  </LocalAlert.Header>
                </LocalAlert>
                <Heading level="2" size="medium">
                  Mismatch
                </Heading>
                <MismatchTable
                  labels={{
                    left: 'Faste data',
                    right: miljo.toUpperCase(),
                  }}
                  mismatch={compared.mismatchFields}
                />
              </>
            ) : (
              <LocalAlert size="small" status="success">
                <LocalAlert.Header>
                  <LocalAlert.Title as="div">Datasettene er funksjonelt like.</LocalAlert.Title>
                </LocalAlert.Header>
              </LocalAlert>
            )}
            <Heading level="2" size="medium">
              Kode
            </Heading>
            <CompareCodeView
              left={{
                code: data.left,
                language: 'json',
                label: 'Faste data',
              }}
              right={{
                code: data.right,
                language: 'json',
                label: miljo.toUpperCase(),
              }}
            />
          </ResultsStack>
        )}
      </SectionStack>
    </AppPage>
  );
};
