import React from 'react';
import styled from 'styled-components';

const HeaderLinkStyle = styled.a<{ isActive: () => boolean }>`
  color: white;
  text-decoration: none;
  font-weight: normal;
  white-space: nowrap;
  margin: 0 10px;
  font-size: 1.6em;
  padding: 20px 10px;
  background-color: ${(props) => (props.isActive() ? '#524c46' : '#3e3832')};

  &:hover {
    background-color: #48423c;
  }
`;

type Props = {
  href: string;
  children: string;
  isActive: () => boolean;
};

const HeaderLink = ({ href, children, isActive }: Props) => (
  <HeaderLinkStyle href={href} isActive={isActive}>
    {children}
  </HeaderLinkStyle>
);

HeaderLink.displayName = 'HeaderLink';

export default HeaderLink;
