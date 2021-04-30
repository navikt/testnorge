import React from 'react';
import styled from 'styled-components';

const HeaderLink = styled.a<{ isActive: () => boolean }>`
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

export default ({ href, children, isActive }: Props) => (
  <HeaderLink href={href} isActive={isActive}>
    {children}
  </HeaderLink>
);
