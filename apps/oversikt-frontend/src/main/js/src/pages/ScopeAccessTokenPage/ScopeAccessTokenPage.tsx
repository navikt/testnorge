import React from "react";
import { useParams } from "react-router-dom";
import FetchAccessToken from "@/components/FetchAccessToken";
import styled from "styled-components";
import { Page } from "@navikt/dolly-komponenter";

const ScopeAccessTokenPage = styled(Page)`
  display: flex;
  justify-content: center;
`;

export default () => {
  // @ts-ignore
  const { scope } = useParams();

  return (
    <ScopeAccessTokenPage>
      <FetchAccessToken
        scope={scope}
        labels={{
          header: "Access Token",
          subHeader: `Generer token fra scope.`,
          description: `Token som kan brukes til å logge på fra scope: ${scope}.`,
        }}
      />
    </ScopeAccessTokenPage>
  );
};
