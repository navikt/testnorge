import React from "react";
import "./App.less";
import { CodeSearch } from "@/components";
import { Header, ProfilLoader } from "@navikt/dolly-komponenter";
import ProfilService from "./service/ProfilService";

function App() {
  return (
    <div className="App">
      <Header
        title="Arbeidsforhold A-melding søk"
        profile={<ProfilLoader {...ProfilService} />}
      />
      <CodeSearch />
    </div>
  );
}

export default App;
