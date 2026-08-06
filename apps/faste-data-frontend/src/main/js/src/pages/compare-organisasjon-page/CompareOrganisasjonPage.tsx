import React from 'react';
import { useParams } from 'react-router-dom';
import { OrganisasjonFasteDataService, OrganisasjonService } from '@/service';
import { CompareCodeView } from '@/components/compare-code-view';
import { OrganisasjonComperator } from '@/comperator';
import {
  SuccessAlertstripe,
  WarningAlertstripe,
  LoadableComponent,
  Page,
} from '@navikt/dolly-komponenter';
import { MismatchTable } from '@/components/mismatch-table';

export default () => {
  const { orgnummer, miljo } = useParams() as { orgnummer: string; miljo: string };

  const onFetch = () =>
    Promise.all([
      OrganisasjonFasteDataService.fetchOrganisasjon(orgnummer),
      OrganisasjonService.fetchOrganisasjon(orgnummer, miljo),
    ]).then((values) => ({
      left: values[0],
      right: values[1],
    }));

  return (
    <Page>
      <h1>Organisasjon differanse</h1>
      <LoadableComponent
        onFetch={onFetch}
        onNotFound={() => (
          <WarningAlertstripe label={`Datasett ikke funnet i miljo ${miljo.toUpperCase()}.`} />
        )}
        render={(data) => {
          if (!data) {
            return null;
          }
          const compared = OrganisasjonComperator.compare(data.left, data.right);
          return (
            <>
              {compared.isMismatch ? (
                <>
                  <WarningAlertstripe label="Det er en mismatch mellom datasettene." />
                  <h2>Mismatch</h2>
                  <MismatchTable
                    labels={{
                      left: 'Faste data',
                      right: miljo.toUpperCase(),
                    }}
                    mismatch={compared.mismatchFields}
                  />
                </>
              ) : (
                <SuccessAlertstripe label="Datasettene er funksjonelt like." />
              )}
              <h2>Kode</h2>
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
            </>
          );
        }}
      />
    </Page>
  );
};
