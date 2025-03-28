import styled from 'styled-components';
import { Light as SyntaxHighlighter } from 'react-syntax-highlighter';
import xml from 'react-syntax-highlighter/dist/esm/languages/hljs/xml';
import { defaultStyle } from 'react-syntax-highlighter/dist/esm/styles/hljs';

SyntaxHighlighter.registerLanguage('xml', xml);

const StyledSyntaxHighlighter = styled(SyntaxHighlighter)`
  font-size: 0.9em;
  width: 100%;
  margin: 0;
`;

type Props = {
  codeString: string;
  wrapLongLines?: boolean;
  language?: string;
};

const PrettyCode = ({ codeString, wrapLongLines = false, language }: Props) => (
  <StyledSyntaxHighlighter
    data-testid={'pretty-code-component'}
    language={language}
    wrapLongLines={wrapLongLines}
    style={defaultStyle}
  >
    {codeString}
  </StyledSyntaxHighlighter>
);

export default PrettyCode;
