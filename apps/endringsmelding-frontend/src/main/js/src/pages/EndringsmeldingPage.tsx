import React from 'react';

import styled from 'styled-components';
import Tabs from 'nav-frontend-tabs';
import Panel from 'nav-frontend-paneler';
import { Page } from '@/components/Page';
import Search from '@/components/Search/Search';

// @ts-ignore
export default () => (
  <Page>
    <Tabs
      tabs={[{ label: 'Fødselsmelding' }, { label: 'Dødsmelding' }]}
      onChange={(_, i) => console.log(i)}
    />
    <Panel border style={{ borderTop: 0, borderTopLeftRadius: 0, borderTopRightRadius: 0 }}>
      <Search
        labels={{
          label: 'Mors ident:',
          button: 'Søk',
        }}
      />
    </Panel>
  </Page>
);
