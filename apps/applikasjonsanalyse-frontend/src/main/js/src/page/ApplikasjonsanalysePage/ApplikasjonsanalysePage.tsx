import React from 'react';
import { LoadableComponent } from '@navikt/dolly-komponenter';
import { Applikasjonsanalyse, fetchDependencies } from '@/service/ApplikasjonsanalyseService';
import AvhengighetsGraph from '@/page/ApplikasjonsanalysePage/AvhengighetsGraph';

const ApplikasjonsanalysePage = () => {
  return (
    <LoadableComponent
      onFetch={fetchDependencies}
      render={(data: Applikasjonsanalyse[]) => <AvhengighetsGraph data={data} />}
    />
  );
};

export default ApplikasjonsanalysePage;
