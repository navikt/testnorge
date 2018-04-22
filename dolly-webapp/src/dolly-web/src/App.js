import 'babel-polyfill';
import React from 'react';
import AppFrame from './AppFrame';
import Routes from './Routes';
import 'normalize.css';
import './index.css';

const App = () => {
    return (
        <AppFrame>
            <Routes/>
        </AppFrame>
    )
};

export default App;
