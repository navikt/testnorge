import React, { useState } from 'react';
import { Box, VStack } from '@navikt/ds-react';
import { SearchProps } from '@/components/Search';
import Search from '@/components/Search/Search';
import SyntaxHighlighter from 'react-syntax-highlighter';
import { a11yDark as customStyle } from 'react-syntax-highlighter/dist/esm/styles/hljs';
import { ScrollArea } from '@/components/layout';

type Props = SearchProps<string>;

const CodeSearch = (props: Props) => {
  const [code, setCode] = useState<string | null>(null);

  return (
    <VStack gap="space-16">
      <Search
        {...props}
        onSearch={(value) => {
          setCode(null);
          return props.onSearch(value).then((response) => {
            setCode(response);
            return response;
          });
        }}
      />
      {code && (
        <Box asChild borderRadius="8">
          <ScrollArea>
            <SyntaxHighlighter customStyle={{ margin: 0 }} language="json" style={customStyle}>
              {code}
            </SyntaxHighlighter>
          </ScrollArea>
        </Box>
      )}
    </VStack>
  );
};

export default CodeSearch;
