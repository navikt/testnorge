import React from 'react';
import { WarningFilled } from '@navikt/ds-icons';
import styled from 'styled-components';

const LoginPage = styled.div`
  margin-top: 150px;
  padding: 100px 20px;
  margin-right: 15%;
  margin-left: 15%;
  background: white;
  border: solid 1px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
`;

export default () => (
  <LoginPage>
    <WarningFilled width={50} height={50} color="#FF9100" />
    <h1>Du har ikke tilgang til denne siden</h1>
    <p>
      Av sikkerhetshensyn må du bestille tilgang for å kunne sende fødselsmeldinger og dødsmeldinger
      fra denne siden.
    </p>
    <p>
      Ta kontakt med team <a href="https://nav-it.slack.com/archives/CA3P9NGA2">#dolly</a> på Slack
      dersom du ønsker tilgang.
    </p>
  </LoginPage>
);
