import { BrowserRouter as Router, Route, Routes } from 'react-router';
import { CompareOrganisasjonPage } from '@/pages/compare-organisasjon-page';
import { FastePersonDataPage } from '@/pages/FastePersonDataPage';
import { FasteOrganiasjonDataPage } from '@/pages/FasteOrganiasjonDataPage';
import { HomePage } from '@/pages/HomePage';
import AppHeader from '@/components/header/AppHeader';
import '@navikt/ds-css';

function App() {
  return (
    <Router>
      <AppHeader />
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
