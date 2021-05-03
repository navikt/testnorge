import React from "react";
import "./App.less";
import { CodeSearch } from "@/components";
import { Header } from "@navikt/dolly-komponenter";

function App() {
  return (
    <div className="App">
      <Header title="Arbeidsforhold A-melding søk" />
      <CodeSearch />
    </div>
  );
}

export default App;
