import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router';
import '@navikt/ds-css';
import { AppHeader } from '@/components/header/AppHeader';
import { EndringsmeldingPage } from '@/pages/endringsmelding-page/EndringsmeldingPage';
import { LoginPage } from '@/pages/login-page/LoginPage';

const App = () => (
  <Router>
    <AppHeader />
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="*" element={<EndringsmeldingPage />} />
    </Routes>
  </Router>
);

export default App;
