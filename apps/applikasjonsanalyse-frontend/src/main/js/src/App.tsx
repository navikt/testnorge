import React from 'react';
import './App.css';
import TestnorgeDependencyPage from '@/page/ApplikasjonsanalysePage';
import { ProfilLoader } from '@/components/profil';
import { Header } from '@navikt/dolly-komponenter';

function App() {
  return (
    <div>
      <Header title="Avheninghetsanalyse">
        <ProfilLoader />
      </Header>
      <div className="body">
        <TestnorgeDependencyPage />
      </div>
    </div>
  );
}

export default App;
