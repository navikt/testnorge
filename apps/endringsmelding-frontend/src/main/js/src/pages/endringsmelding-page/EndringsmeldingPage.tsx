import React, { useState } from 'react';
import Tabs from 'nav-frontend-tabs';
import { Page } from '@navikt/dolly-komponenter';
import { FodselsmeldingForm, DodsmeldingForm } from './form';
import Panel from 'nav-frontend-paneler';

export default () => {
  const [tapIndex, setTabIndex] = useState(0);
  return (
    <Page>
      <Tabs
        tabs={[{ label: 'Fødselsmelding' }, { label: 'Dødsmelding' }]}
        onChange={(_, index) => setTabIndex(index)}
      />
      <Panel border style={{ borderTop: 0, borderTopLeftRadius: 0, borderTopRightRadius: 0 }}>
        {tapIndex === 0 ? <FodselsmeldingForm /> : <DodsmeldingForm />}
      </Panel>
    </Page>
  );
};
