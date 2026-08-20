import React from 'react';
import { Tabs } from '@navikt/ds-react';
import styled from 'styled-components';
import { FodselsmeldingForm } from '@/pages/endringsmelding-page/form/fodselsmelding-from/FodselsmeldingForm';
import { DodsmeldingForm } from '@/pages/endringsmelding-page/form/dodsmelding-form/DodsmeldingForm';

const PageContent = styled.main`
  margin-top: calc(var(--ax-space-24) + var(--ax-space-28));
  margin-inline: 25%;

  @media (max-width: 80rem) {
    margin-inline: 18%;
  }

  @media (max-width: 48rem) {
    margin-top: var(--ax-space-32);
    margin-inline: 5%;
  }
`;

const StyledTabsPanel = styled(Tabs.Panel)`
  background-color: var(--ax-bg-default);
  padding: var(--ax-space-16);
  border-top: none;
  border-radius: var(--ax-radius-4);
  border-top-left-radius: 0;
  border-top-right-radius: 0;
`;

export const EndringsmeldingPage = () => {
  return (
    <PageContent>
      <Tabs defaultValue="fødselsmelding" size="medium">
        <Tabs.List>
          <Tabs.Tab value="fødselsmelding" label="Fødselsmelding" />
          <Tabs.Tab value="dødsmelding" label="Dødsmelding" />
        </Tabs.List>
        <StyledTabsPanel value="fødselsmelding">
          <FodselsmeldingForm />
        </StyledTabsPanel>
        <StyledTabsPanel value="dødsmelding">
          <DodsmeldingForm />
        </StyledTabsPanel>
      </Tabs>
    </PageContent>
  );
};
