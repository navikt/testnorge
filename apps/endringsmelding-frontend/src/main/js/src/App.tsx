import React from 'react';

import { Header, ProfilLoader } from '@navikt/dolly-komponenter';
import { EndringsmeldingPage } from '@/pages';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import LoginPage from '@/pages/login-page/LoginPage';
import ProfilService from './service/ProfilService';

const App = () => (
  <Router>
    <Header title="Endringsmeldinger" profile={<ProfilLoader {...ProfilService} />} />
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/" element={<EndringsmeldingPage />} />
    </Routes>
  </Router>
);

export default App;
