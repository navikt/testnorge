import React from 'react';
import styled from 'styled-components';

const HeaderLinkStyle = styled.a`
  color: white;
  text-decoration: none;
  font-weight: normal;
  white-space: nowrap;
  margin: 0 10px;
  font-size: 1.6em;
  padding: 20px 10px;

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
  <HeaderLinkStyle style={{ backgroundColor: isActive() ? '#524c46' : '#3e3832' }} href={href}>
    {children}
  </HeaderLinkStyle>
);

HeaderLink.displayName = 'HeaderLink';

export default HeaderLink;
