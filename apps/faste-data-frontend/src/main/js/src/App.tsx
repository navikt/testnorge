import React from 'react';
import { Header } from '@/components/header';
import { HashRouter as Router, Switch, Route } from 'react-router-dom';
import { FasteDataPage } from '@/pages';
import { ProfilLoader } from '@/components/profil';
import { CompareOrganisasjonPage } from '@/pages/compare-organisasjon-page';

function App() {
  return (
    <Router>
      <Switch>
        <Route path="/organisasjon/:orgnummer/:miljo">
          <Header title="Faste Data">
            <ProfilLoader />
          </Header>
          <CompareOrganisasjonPage />
        </Route>
        <Route path="/">
          <Header title="Faste Data">
            <ProfilLoader />
          </Header>
          <FasteDataPage />
        </Route>
      </Switch>
    </Router>
  );
}

export default App;
