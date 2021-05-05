import React from 'react';

import { Header, ProfilLoader } from '@navikt/dolly-komponenter';
import { EndringsmeldingPage } from '@/pages';
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom';
import LoginPage from '@/pages/login-page/LoginPage';
import ProfilService from './service/ProfilService';

const App = () => (
  <div>
    <Header title="Endringsmeldinger" profile={<ProfilLoader {...ProfilService} />} />
    <Router>
      <Switch>
        <Route path="/login">
          <LoginPage />
        </Route>
        <Route path="/">
          <EndringsmeldingPage />
        </Route>
      </Switch>
    </Router>
  </div>
);

export default App;
