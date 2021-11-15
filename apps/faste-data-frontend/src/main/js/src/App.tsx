import React from 'react';
import { Header, HeaderLink, HeaderLinkGroup, ProfilLoader } from '@navikt/dolly-komponenter';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { CompareOrganisasjonPage } from '@/pages/compare-organisasjon-page';
import ProfilService from './service/ProfilService';
import { FastePersonDataPage } from '@/pages/FastePersonDataPage';
import { FasteOrganiasjonDataPage } from '@/pages/FasteOrganiasjonDataPage';
import { HomePage } from '@/pages/HomePage';

function App() {
  return (
    <Router>
      <Header
        title="Faste Data"
        profile={
          <ProfilLoader
            fetchProfil={ProfilService.fetchProfil}
            fetchBilde={ProfilService.fetchBilde}
          />
        }
      >
        <HeaderLinkGroup>
          <HeaderLink href="/" isActive={() => window.location.pathname == '/'}>
            Hjem
          </HeaderLink>

          <HeaderLink href="/person" isActive={() => window.location.pathname == '/person'}>
            Person
          </HeaderLink>
          <HeaderLink
            href="/organisasjon"
            isActive={() => window.location.pathname == '/organisasjon'}
          >
            Organiasjon
          </HeaderLink>
        </HeaderLinkGroup>
      </Header>

      <Routes>
        <Route path="/organisasjon/:orgnummer/:miljo" element={<CompareOrganisasjonPage />} />
        <Route path="/organisasjon" element={<FasteOrganiasjonDataPage />} />
        <Route path="/person" element={<FastePersonDataPage />} />
        <Route path="/" element={<HomePage />} />
      </Routes>
    </Router>
  );
}

export default App;
