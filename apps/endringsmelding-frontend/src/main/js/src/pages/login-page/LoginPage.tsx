import React from 'react';
import { ExclamationmarkTriangleFillIcon } from '@navikt/aksel-icons';
import { BodyShort, Heading, Link } from '@navikt/ds-react';
import styled from 'styled-components';

const LoginSurface = styled.main`
  margin-top: calc(var(--ax-space-96) + var(--ax-space-48) + var(--ax-space-6));
  margin-inline: 15%;
  padding: calc(var(--ax-space-96) + var(--ax-space-4)) var(--ax-space-20);
  border: var(--ax-space-1) solid var(--ax-border-neutral);
  background-color: var(--ax-bg-default);
  display: flex;
  justify-content: center;

  @media (max-width: 80rem) {
    margin-inline: 10%;
  }

  @media (max-width: 48rem) {
    margin-top: var(--ax-space-64);
    margin-inline: 5%;
    padding: var(--ax-space-48) var(--ax-space-16);
  }
`;

const LoginContent = styled.div`
  width: 100%;
  max-width: 38rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  row-gap: var(--ax-space-16);
  text-align: center;
`;

export const LoginPage = () => (
  <LoginSurface>
    <LoginContent>
      <ExclamationmarkTriangleFillIcon
        title="Advarsel"
        style={{ fontSize: '3rem', color: 'var(--ax-text-warning-decoration)' }}
      />
      <Heading size="large" level="1">
        Du har ikke tilgang til denne siden
      </Heading>
      <BodyShort size="large">
        Av sikkerhetshensyn må du bestille tilgang for å kunne sende fødselsmeldinger og
        dødsmeldinger fra denne siden.
      </BodyShort>
      <BodyShort size="large">
        Ta kontakt med team{' '}
        <Link href="https://nav-it.slack.com/archives/CA3P9NGA2" target="_blank" rel="noreferrer">
          #dolly
        </Link>{' '}
        på Slack dersom du ønsker tilgang.
      </BodyShort>
    </LoginContent>
  </LoginSurface>
);
