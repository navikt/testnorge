import React from 'react';
import './App.less';
import {CodeView} from "@/componetes";

function App() {
  return (
    <div className="App">
      <h1>Arbeidsforhold</h1>
        <CodeView onNext={() => { window.alert("NEste test")}} />
    </div>
  );
}

export default App;
