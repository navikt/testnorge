import React, { useState } from 'react';
import Tabs from 'nav-frontend-tabs';
import { Page } from '@/components/page';
import { FodselsmeldingPanel } from './fodselsmelding-panel';
import { DodsmeldingPanel } from '@/pages/endringsmelding-page/dodsmelding-panel';

// @ts-ignore
export default () => {
  const [tapIndex, setTabIndex] = useState(0);
  return (
    <Page>
      <Tabs
        tabs={[{ label: 'Fødselsmelding' }, { label: 'Dødsmelding' }]}
        onChange={(_, index) => setTabIndex(index)}
      />
      {tapIndex === 0 ? <FodselsmeldingPanel /> : <DodsmeldingPanel />}
    </Page>
  );
};
