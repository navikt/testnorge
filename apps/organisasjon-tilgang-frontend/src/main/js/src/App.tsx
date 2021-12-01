import React from 'react';

import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { Header } from '@navikt/dolly-komponenter';
import { HomePage } from '@/pages';

const App = () => (
  <Router>
    <Header title="Organisasjon Tilgang" />
    <Routes>
      <Route path="/" element={<HomePage />} />
    </Routes>
  </Router>
);

export default App;
