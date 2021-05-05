import React from 'react';
import { Header, ProfilLoader } from '@navikt/dolly-komponenter';
import ProfilService from './service/ProfilService';
import ApplikasjonsanalysePage from '@/page/ApplikasjonsanalysePage';

const App = () => {
  return (
    <div>
      <Header title="Avheninghetsanalyse" profile={<ProfilLoader {...ProfilService} />} />
      <ApplikasjonsanalysePage />
    </div>
  );
};

export default App;
