import React from 'react';

import { BrowserRouter as Router, Route, Routes } from 'react-router';
import AppHeader from './components/AppHeader';
import { LoginPage } from './pages/login-page/LoginPage';
import { TpsMeldingerPage } from './pages/tpsmeldinger-page/TpsMeldingerPage';

const App = () => (
  <Router>
    <AppHeader />
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="*" element={<TpsMeldingerPage />} />
    </Routes>
  </Router>
);

export default App;
