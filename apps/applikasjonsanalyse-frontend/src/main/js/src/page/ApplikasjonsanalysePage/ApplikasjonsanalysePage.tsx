import React from 'react';
import { LoadableComponent, Page } from '@navikt/dolly-komponenter';
import { Applikasjonsanalyse, fetchDependencies } from '@/service/ApplikasjonsanalyseService';
import AvhengighetsGraph from '@/page/ApplikasjonsanalysePage/AvhengighetsGraph';

const ApplikasjonsanalysePage = () => (
  <Page>
    <LoadableComponent
      onFetch={fetchDependencies}
      render={(data: Applikasjonsanalyse[]) => <AvhengighetsGraph data={data} />}
    />
  </Page>
);

export default ApplikasjonsanalysePage;
