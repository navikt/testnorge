import React from "react";
import styled from "styled-components";
// @ts-ignore
import { navLogo } from "@navikt/dolly-assets";

const Header = styled.header`
  display: flex;
  background-color: #3e3832;
`;

const Logo = styled.img`
  padding: 20px 10px;
  margin: 0 20px;
  height: 30px;
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

type Props = {
  title: string;
  profile?: React.ReactNode;
  children?: React.ReactNode;
};

export default ({ children, title, profile }: Props) => (
  <Header>
    <Logo src={navLogo} alt="Nav logo" />
    <VerticalLine />
    <Title>{title}</Title>

    {React.Children.map(children, (child) => (
      <>
        <VerticalLine />
        <Container>{child}</Container>
      </>
    ))}
    {profile && (
      <>
        <VerticalLine />
        {profile}
      </>
    )}
  </Header>
);
