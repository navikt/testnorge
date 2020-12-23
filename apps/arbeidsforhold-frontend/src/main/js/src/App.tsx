import React from "react";
import "./App.less";
import { CodeView, FetchCode } from "@/componetes";
import Api from "@/api";

function App() {
  return (
    <div className="App">
      <h1>Arbeidsforhold</h1>
      <FetchCode
        fetchFromPosition={(item) =>
          Api.fetch({
            url: "/api/v1/oppsummeringsdokumenter/raw/items/" + item,
            method: "GET",
          })
        }
      />
    </div>
  );
}

export default App;
