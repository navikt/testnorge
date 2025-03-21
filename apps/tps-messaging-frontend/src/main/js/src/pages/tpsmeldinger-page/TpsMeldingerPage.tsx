import React from 'react';
import {Page, SelectFormItem} from '@navikt/dolly-komponenter';
import {Tabs} from '@navikt/ds-react';
import styled from 'styled-components';
import {getQueues} from "@/service/SendTpsMeldingService";

const StyledPanel = styled(Tabs.Panel)`
  background-color: hsl(0deg 0% 100%);
  padding: 1rem 1rem 1rem 1rem;
  border-radius: 4px;
`;

export const TpsMeldingerPage = () => {
  var queses = getQueues();

  return (
    <Page>
      <SelectFormItem
          label="Meldingskøer"
          htmlId="melding-queue-select"
          onChange={(value) => setIdentType(value && value.length > 0 ? value[0] : '')}
          options={queues}
      />
      {/*<Tabs defaultValue="fødselsmelding" size="medium">*/}
      {/*  <Tabs.List>*/}
      {/*    <Tabs.Tab value="fødselsmelding" label="Fødselsmelding 1" />*/}
      {/*    <Tabs.Tab value="dødsmelding" label="Dødsmelding 1" />*/}
      {/*  </Tabs.List>*/}
      {/*  <StyledPanel*/}
      {/*    value={'fødselsmelding'}*/}
      {/*    style={{ borderTop: 0, borderTopLeftRadius: 0, borderTopRightRadius: 0 }}*/}
      {/*  >*/}
      {/*    <FodselsmeldingForm />*/}
      {/*  </StyledPanel>*/}
      {/*  <StyledPanel*/}
      {/*    value={'dødsmelding'}*/}
      {/*    style={{ borderTop: 0, borderTopLeftRadius: 0, borderTopRightRadius: 0 }}*/}
      {/*  >*/}
      {/*    <DodsmeldingForm />*/}
      {/*  </StyledPanel>*/}
      {/*</Tabs>*/}
    </Page>
  );
};

