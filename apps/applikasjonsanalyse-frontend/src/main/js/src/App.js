import React from 'react';
import './App.css';
import TestnorgeDependencyGraph from "./TestnorgeDependencyGraph";

function App() {
    return (
        <div className="app">
            <div>
                <h1>Avheninghetsanalyse</h1>
                <h2>Testnorge</h2>
                <TestnorgeDependencyGraph/>
            </div>
        </div>
    );
}

export default App;
