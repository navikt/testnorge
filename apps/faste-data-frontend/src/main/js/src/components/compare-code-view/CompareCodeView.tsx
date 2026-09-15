import React from 'react';
import { CodeView } from '@/components/code-view';
import { ResponsiveSplit } from '@/components/layout';

type Props = {
  left: {
    code: string | object;
    language: string;
    label: string;
  };
  right: {
    code: string | object;
    language: string;
    label: string;
  };
};

export default ({ left, right }: Props) => {
  return (
    <ResponsiveSplit>
      <CodeView {...left} />
      <CodeView {...right} />
    </ResponsiveSplit>
  );
};
