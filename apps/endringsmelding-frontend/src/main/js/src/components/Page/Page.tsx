import React from 'react';

import styled from 'styled-components';

const Page = styled.div`
  margin-top: 50px;
  margin-right: 15%;
  margin-left: 15%;
`;

type Props = {
  children?: React.ReactNode;
};

export default ({ children }: Props) => <Page>{children}</Page>;
