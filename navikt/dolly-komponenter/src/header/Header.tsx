import React from 'react';
import styled from 'styled-components';
// @ts-ignore
import { navLogo } from '@navikt/dolly-assets';

const Header = styled.header`
  padding: 15px 40px;
  display: flex;
  background-color: #3e3832;
  border: solid transparent;
`;

const Logo = styled.img`
  height: 30px;
`;

const VerticalLine = styled.div`
  height: 30px;
  border-right: 2px solid #88b1d6;
  margin-left: 40px;
  margin-right: 40px;
`;

const Title = styled.h1`
  margin: 0;
  width: 100%;
  font-size: 1.6rem;
  color: white;
`;

type Props = {
  title: string;
  children?: React.ReactNode;
};

export default ({ children, title }: Props) => (
  <Header>
    <Logo src={navLogo} alt="Nav logo" />
    <VerticalLine />
    <Title>{title}</Title>
    {React.Children.map(children, (child) => (
      <>
        <VerticalLine />
        {child}
      </>
    ))}
  </Header>
);
