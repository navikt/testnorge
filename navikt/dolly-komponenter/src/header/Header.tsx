import React from 'react';
import styled from 'styled-components';
// @ts-ignore
import { navLogo } from '@navikt/dolly-assets';

const StyledHeader = styled.header`
  max-height: 70px;
  display: flex;
  background-color: #3e3832;
`;

const Logo = styled.img`
  margin: 15px;
  height: 40px;
`;

const VerticalLine = styled.div`
  border-right: 2px solid #88b1d6;
  margin: 15px 20px;
`;

const Title = styled.h1`
  margin: 0;
  padding: 20px 10px;
  white-space: nowrap;
  font-size: 1.6rem;
  color: white;
`;

const Container = styled.div`
  width: 100%;
`;

export type HeaderProps = {
  title: string;
  profile?: React.ReactNode;
  children?: React.ReactNode;
};

const Header = ({ children, title, profile }: HeaderProps) => (
  <StyledHeader>
    <Logo src={navLogo} alt="Nav logo" />
    <VerticalLine />
    <Title>{title}</Title>
    {React.Children.count(children) > 0 ? (
      React.Children.map(children, (child) => (
        <>
          <VerticalLine />
          <Container>{child}</Container>
        </>
      ))
    ) : (
      <Container />
    )}

    {profile && (
      <>
        <VerticalLine />
        {profile}
      </>
    )}
  </StyledHeader>
);

Header.displayName = 'Header';

export default Header;
