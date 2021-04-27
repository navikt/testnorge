import React from 'react';

import { Header } from '@navikt/dolly-komponenter';
import { EndringsmeldingPage } from '@/pages';
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom';
import LoginPage from '@/pages/login-page/LoginPage';

function App() {
  console.log(Header);

  return (
    <Router>
      <Switch>
        <Route path="/login">
          <Header />
          <LoginPage />
        </Route>
        <Route path="/">
          <Header />
          <EndringsmeldingPage />
        </Route>
      </Switch>
    </Router>
  );
}

export default App;
