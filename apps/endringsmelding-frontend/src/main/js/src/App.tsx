import React from 'react';

import { Header } from '@/components/header';
import { ProfilLoader } from '@/components/profil';
import { EndringsmeldingPage } from '@/pages';
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom';
import LoginPage from '@/pages/login-page/LoginPage';

function App() {
  return (
    <Router>
      <Switch>
        <Route path="/page/login">
          <Header />
          <LoginPage />
        </Route>
        <Route path="/">
          <Header>
            <ProfilLoader />
          </Header>
          <EndringsmeldingPage />
        </Route>
      </Switch>
    </Router>
  );
}

export default App;
