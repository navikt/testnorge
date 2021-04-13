import React from 'react';
import { CodeView } from '@/components/code-view';
import styled from 'styled-components';

const CompareCodeView = styled.div`
  display: flex;
`;

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
    <div>
      <CompareCodeView>
        <CodeView {...left} />
        <CodeView {...right} />
      </CompareCodeView>
    </div>
  );
};
