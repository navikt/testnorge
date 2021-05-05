import React, { useState } from 'react';
import './App.css';
import TestnorgeDependencyPage from '@/page/ApplikasjonsanalysePage';
import { Header, ProfilLoader } from '@navikt/dolly-komponenter';
import ProfilService from './service/ProfilService';

const App = () => {
  return (
    <div>
      <Header title="Avheninghetsanalyse" profile={<ProfilLoader {...ProfilService} />} />
      <div className="body">{/*<TestnorgeDependencyPage />*/}</div>
    </div>
  );
};

export default App;
