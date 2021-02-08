import React from 'react';
import Panel from 'nav-frontend-paneler';
import Search from '@/components/search/Search';
import { Page } from '@/components/page';

export default () => (
  <Panel border style={{ borderTop: 0, borderTopLeftRadius: 0, borderTopRightRadius: 0 }}>
    <Search
      onSearch={(value) =>
        new Promise<string>((resolve, reject) => setTimeout(() => reject(value), 1000))
      }
      labels={{
        label: 'Mors ident:',
        button: 'Søk',
        onFound: 'Person funnet',
        onNotFound: 'Person ikke funnet',
      }}
    />
  </Panel>
);
