import React from 'react';
import { Heading } from '@navikt/ds-react';
import { a11yDark as customStyle } from 'react-syntax-highlighter/dist/esm/styles/hljs';
import SyntaxHighlighter from 'react-syntax-highlighter';
import styled from 'styled-components';
import { ScrollArea } from '@/components/layout';

type Props = {
  code: string | object;
  language: string;
  label: string;
};

const CodeView = styled.div`
  width: 100%;
  min-width: 0;
`;

const Label = styled(Heading)`
  text-align: center;
  margin: 0;
`;

export default ({ code, language, label }: Props) => {
  const formattedCode = typeof code === 'string' ? code : JSON.stringify(code, null, 2);

  return (
    <CodeView>
      <Label level="3" size="small">
        {label}
      </Label>
      <ScrollArea>
        <SyntaxHighlighter customStyle={{ margin: 0 }} language={language} style={customStyle}>
          {formattedCode}
        </SyntaxHighlighter>
      </ScrollArea>
    </CodeView>
  );
};
