import React from 'react';
import styled from 'styled-components';

type Props = {
  children: React.ReactNode;
};

const HeaderLinksGroupStyle = styled.div`
  display: flex;
  justify-content: center;
`;

const HeaderLinksGroup = ({ children }: Props) => (
  <HeaderLinksGroupStyle>{children}</HeaderLinksGroupStyle>
);

HeaderLinksGroup.displayName = 'HeaderLinksGroup';

export default HeaderLinksGroup;
