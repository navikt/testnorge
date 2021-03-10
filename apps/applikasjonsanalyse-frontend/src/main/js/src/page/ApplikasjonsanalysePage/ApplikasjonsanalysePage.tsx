import React from 'react';
import { LoadableComponent } from '@/component';
import { Applikasjonsanalyse, fetchDependencies } from '@/service/ApplikasjonsanalyseService';
import AvheninghetsGraph from '@/page/ApplikasjonsanalysePage/AvheninghetsGraph';

const ApplikasjonsanalysePage = () => {
  return (
    <LoadableComponent
      onFetch={fetchDependencies}
      render={(data: Applikasjonsanalyse[]) => <AvheninghetsGraph data={data} />}
    />
  );
};

export default ApplikasjonsanalysePage;
