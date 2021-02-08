import React from 'react';

import styled from 'styled-components';
import Tabs from 'nav-frontend-tabs';
import Panel from 'nav-frontend-paneler';
import { Page } from '@/components/page';
import Search from '@/components/search/Search';
import FodselsmeldingPanel from './FodselsmeldingPanel';

// @ts-ignore
export default () => (
  <Page>
    <Tabs
      tabs={[{ label: 'Fødselsmelding' }, { label: 'Dødsmelding' }]}
      onChange={(_, i) => console.log(i)}
    />
    <FodselsmeldingPanel />
  </Page>
);
