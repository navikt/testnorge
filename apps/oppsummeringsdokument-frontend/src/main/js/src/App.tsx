import React from "react";
import "./App.less";
import { CodeSearch } from "@/components";
import { Header, Page, ProfilLoader } from "@navikt/dolly-komponenter";
import ProfilService from "./service/ProfilService";

function App() {
  return (
    <div className="App">
      <Header title="A-melding" profile={<ProfilLoader {...ProfilService} />} />
      <Page>
        <CodeSearch />
      </Page>
    </div>
  );
}

export default App;
