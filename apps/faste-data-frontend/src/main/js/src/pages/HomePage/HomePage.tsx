import React from 'react';
import { BodyLong, Heading, VStack } from '@navikt/ds-react';
import { AppPage, SectionStack } from '@/components/layout';

const HomePage = () => {
  return (
    <AppPage>
      <SectionStack>
        <VStack gap="space-16">
          <Heading level="1" size="large">
            Faste Data Søk
          </Heading>
          <BodyLong>Appen brukes til søke etter faste data i test miljøer.</BodyLong>
        </VStack>
      </SectionStack>
    </AppPage>
  );
};

export default HomePage;
