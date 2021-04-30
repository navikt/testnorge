import React, { useContext, useEffect, useState } from "react";
// @ts-ignore
import { CopyToClipboard } from "react-copy-to-clipboard/lib/Component";
// @ts-ignore
import ApplicationService from "@/services/ApplicationService";
import { Hovedknapp, Knapp } from "nav-frontend-knapper";
import SessionTimer from "@/components/SessionTimer";
import styled from "styled-components";
import { ErrorAlertstripe } from "@navikt/dolly-komponenter";

type Props = {
  name: string;
  labels?: {
    header?: string;
    subHeader?: string;
    description?: string;
  };
};

const ButtonGroup = styled.div`
  display: flex;
  justify-content: center;
`;

const FetchAccessToken = styled.div`
  max-width: 500px;
  padding-bottom: 25px;
  text-align: center;
`;

const GetToken = styled(Hovedknapp)`
  margin: 10px 5px;
`;

const CopyToken = styled(Knapp)`
  margin: 10px 5px;
`;

const AccessTokenTextArea = styled.textarea`
  min-height: 200px;
  min-width: 494px;
  text-align: left;
  resize: vertical;
  background: white;
`;

export default ({ name, labels = {} }: Props) => {
  const [accessToken, setAccessToken] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(false);

  const onGetToken = (name: string) => {
    setLoading(true);
    setError(false);
    setAccessToken(null);
    ApplicationService.fetchToken(name)
      .then((response: any) => {
        setAccessToken(response.token);
        setLoading(false);
      })
      .catch(() => {
        setError(true);
        setLoading(false);
      });
  };

  return (
    <FetchAccessToken>
      {labels.header && <h1>{labels.header}</h1>}
      {labels.subHeader && <h2>{labels.subHeader}</h2>}
      {labels.description && <p>{labels.description}</p>}
      <AccessTokenTextArea
        disabled={true}
        value={loading ? "Laster token..." : accessToken ? accessToken : ""}
      />
      <SessionTimer />
      {error && <ErrorAlertstripe label="Noe gikk galt. Prøv på nytt." />}
      <ButtonGroup>
        <GetToken disabled={loading} onClick={() => onGetToken(name)}>
          Hent token
        </GetToken>
        <CopyToClipboard text={accessToken}>
          <CopyToken disabled={loading}>Copy</CopyToken>
        </CopyToClipboard>
      </ButtonGroup>
    </FetchAccessToken>
  );
};
