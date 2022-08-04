import React from 'react';
import { Page } from '@navikt/dolly-komponenter';
import { DodsmeldingForm, FodselsmeldingForm } from './form';
import { Tabs } from '@navikt/ds-react';
import styled from 'styled-components';

const StyledPanel = styled(Tabs.Panel)`
  background-color: hsl(0deg 0% 100%);
  padding: 1rem 1rem 1rem 1rem;
  border-radius: 4px;
`;

export default () => {
  return (
    <Page>
      <Tabs defaultValue="fødselsmelding" size="medium">
        <Tabs.List>
          <Tabs.Tab value="fødselsmelding" label="Fødselsmelding" />
          <Tabs.Tab value="dødsmelding" label="Dødsmelding" />
        </Tabs.List>
        <StyledPanel
          value={'fødselsmelding'}
          style={{ borderTop: 0, borderTopLeftRadius: 0, borderTopRightRadius: 0 }}
        >
          <FodselsmeldingForm />
        </StyledPanel>
        <StyledPanel
          value={'dødsmelding'}
          style={{ borderTop: 0, borderTopLeftRadius: 0, borderTopRightRadius: 0 }}
        >
          <DodsmeldingForm />
        </StyledPanel>
      </Tabs>
    </Page>
  );
};
