import React from 'react';

import styled from 'styled-components';

const Page = styled.div`
  margin-top: 50px;
  margin-right: 25%;
  margin-left: 25%;
`;

type Props = {
  children?: React.ReactNode;
};

export default ({ children, ...props }: Props) => <Page {...props}>{children}</Page>;
