import React from "react";

import FetchAccessToken from "@/components/FetchAccessToken";
import { Page } from "@/components/page";
import styled from "styled-components";

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
        subHeader: "Genererer lokalt utviklingstoken",
        description:
          "Dette token skal fungere for alle apper som kjøres lokalt.",
      }}
    />
  </MagicTokenPage>
);
