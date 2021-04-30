import React from "react";
import styled from "styled-components";

type Props = {
  children: React.ReactNode;
};

const HeaderLinksGroup = styled.div`
  display: flex;
  justify-content: center;
`;
export default ({ children }: Props) => (
  <HeaderLinksGroup>{children}</HeaderLinksGroup>
);
