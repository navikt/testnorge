import React, { useState } from 'react';
import { SearchProps } from '@/components/Search';
import Search from '@/components/Search/Search';
import SyntaxHighlighter from 'react-syntax-highlighter';
import { a11yDark as customStyle } from 'react-syntax-highlighter/dist/esm/styles/hljs';

type Props = SearchProps<string>;

const CodeSearch = <T extends unknown>(props: Props) => {
  const [code, setCode] = useState(null);

  return (
    <div>
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
        <SyntaxHighlighter language="json" style={customStyle}>
          {code}
        </SyntaxHighlighter>
      )}
    </div>
  );
};

export default CodeSearch;
