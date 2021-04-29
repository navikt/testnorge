import React from 'react';
import { Page } from '@navikt/dolly-komponenter';
import { OrganisasjonForm } from './form';

export default () => (
  <Page>
    <h1>Faste organisasjoner</h1>
    <OrganisasjonForm />
  </Page>
);
