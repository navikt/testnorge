import React from 'react';
import { a11yDark as customStyle } from 'react-syntax-highlighter/dist/esm/styles/hljs';
import SyntaxHighlighter from 'react-syntax-highlighter';
import styled from 'styled-components';

type Props = {
  code: string | object;
  language: string;
  label: string;
};

const CodeView = styled.div`
  width: 50%;
  margin: 1px;
`;

const Label = styled.h3`
  text-align: center;
`;

export default ({ code, language, label }: Props) => {
  const _code = JSON.stringify(code, null, 2);
  return (
    <CodeView>
      <Label>{label}</Label>
      <SyntaxHighlighter language={language} style={customStyle}>
        {_code}
      </SyntaxHighlighter>
    </CodeView>
  );
};
