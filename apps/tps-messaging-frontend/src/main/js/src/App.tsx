import React from 'react';

import { Header, ProfilLoader } from '@navikt/dolly-komponenter';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import ProfilService from './service/ProfilService';
import '@navikt/ds-css';
import { LoginPage } from './pages/login-page/LoginPage';
import { TpsMeldingerPage } from './pages/tpsmeldinger-page/TpsMeldingerPage';

const App = () => (
  <Router>
    <Header title="Meldinger til TPS" profile={<ProfilLoader {...ProfilService} />} />
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="*" element={<TpsMeldingerPage />} />
    </Routes>
  </Router>
);

export default App;
