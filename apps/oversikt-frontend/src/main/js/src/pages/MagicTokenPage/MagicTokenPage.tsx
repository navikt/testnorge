import React from "react";

import "./MagicTokenPage.less";
import Page from "@/components/Page";
import FetchAccessToken from "@/components/FetchAccessToken";

const MagicTokenPage = () => (
  <Page title="Magic Token">
    <h2>Genererer lokalt utviklingstoken</h2>
    <p>Dette token skal fungere for alle apper som kjøres lokalt.</p>
    <FetchAccessToken name="team-dolly-lokal-app" />
  </Page>
);

export default MagicTokenPage;
