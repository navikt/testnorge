import React from 'react';
import { Header, ProfilLoader } from '@navikt/dolly-komponenter';
import { HashRouter as Router, Switch, Route } from 'react-router-dom';
import { FasteDataPage } from '@/pages';
import { CompareOrganisasjonPage } from '@/pages/compare-organisasjon-page';
import ProfilService from './service/ProfilService';

function App() {
  return (
    <div>
      <Header
        title="Faste Data"
        profile={
          <ProfilLoader
            fetchProfil={ProfilService.fetchProfil}
            fetchBilde={ProfilService.fetchBilde}
          />
        }
      />
      <Router>
        <Switch>
          <Route path="/organisasjon/:orgnummer/:miljo">
            <CompareOrganisasjonPage />
          </Route>
          <Route path="/">
            <FasteDataPage />
          </Route>
        </Switch>
      </Router>
    </div>
  );
}

export default App;
