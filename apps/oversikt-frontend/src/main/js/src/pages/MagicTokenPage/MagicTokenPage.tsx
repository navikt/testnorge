import React from "react";

import FetchAccessToken from "@/components/FetchAccessToken";
import styled from "styled-components";
import { Page } from "@navikt/dolly-komponenter";

const MagicTokenPage = styled(Page)`
  display: flex;
  justify-content: center;
`;

export default () => (
  <MagicTokenPage>
    <FetchAccessToken
      name="team-dolly-lokal-app"
      labels={{
        header: "Magic Token",
        subHeader: "Generer lokalt utviklingstoken",
        description:
          "Dette token skal fungere for alle apper som kjøres lokalt.",
      }}
    />
  </MagicTokenPage>
);
