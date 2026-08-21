import React from 'react';
import { ExclamationmarkTriangleFillIcon } from '@navikt/aksel-icons';
import { BodyShort, Box, Heading, Link, Page, VStack } from '@navikt/ds-react';

export const LoginPage = () => (
  <Page contentBlockPadding="none">
    <Page.Block as="main" gutters>
      <div className="tps-login-page">
        <Box
          className="tps-login-page__card"
          background="default"
          borderWidth="1"
          borderColor="neutral-subtle"
          paddingBlock="space-96"
          paddingInline="space-20"
        >
          <VStack gap="space-16" align="center">
            <ExclamationmarkTriangleFillIcon
              title="Advarsel"
              style={{ fontSize: '3rem', color: 'var(--ax-text-warning-decoration)' }}
            />
            <Heading level="1" size="large">
              Du har ikke tilgang til denne siden
            </Heading>
            <BodyShort>
              Av sikkerhetshensyn må du bestille tilgang for å kunne sende meldinger til TPS.
            </BodyShort>
            <BodyShort>
              Ta kontakt med team{' '}
              <Link href="https://nav-it.slack.com/archives/CA3P9NGA2">#dolly</Link> på Slack eller
              på epost dolly@nav.no dersom du ønsker tilgang.
            </BodyShort>
          </VStack>
        </Box>
      </div>
    </Page.Block>
  </Page>
);
