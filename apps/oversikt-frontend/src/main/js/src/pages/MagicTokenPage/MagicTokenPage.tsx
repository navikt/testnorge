import React from "react";

import "./MagicTokenPage.less";
import Page from "@/components/Page";
import FetchAccessToken from "@/components/FetchAccessToken";

const MagicTokenPage = () => (
  <Page title="Magic Token's">
    <h2>Lokal utviklings token:</h2>
    <p>Denne token skal fungere for alle apper som blir kjørt lokalt.</p>
    <FetchAccessToken name="team-dolly-lokal-app" />
  </Page>
);

export default MagicTokenPage;
