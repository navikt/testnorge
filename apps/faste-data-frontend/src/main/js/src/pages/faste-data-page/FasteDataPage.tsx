import React from 'react';
import { Page } from '@navikt/dolly-komponenter';
import { OrganisasjonForm, PersonForm } from './form';

export default () => (
  <Page>
    <h1>Faste data</h1>
    <OrganisasjonForm />
    <PersonForm />
  </Page>
);
