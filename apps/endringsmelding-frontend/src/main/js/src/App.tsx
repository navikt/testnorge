import React from 'react';

import { Header, ProfilLoader } from '@navikt/dolly-komponenter';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import ProfilService from './service/ProfilService';
import '@navikt/ds-css';
import { EndringsmeldingPage } from '@/pages/endringsmelding-page/EndringsmeldingPage';
import { LoginPage } from '@/pages/login-page/LoginPage';

const App = () => (
  <Router>
    <Header title="Endringsmeldinger" profile={<ProfilLoader {...ProfilService} />} />
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="*" element={<EndringsmeldingPage />} />
    </Routes>
  </Router>
);

export default App;
