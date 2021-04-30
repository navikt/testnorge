import React from "react";
import { Link, useLocation } from "react-router-dom";
import styled from "styled-components";

const HeaderLink = styled(Link)<{ isActive: () => boolean }>`
  color: white;
  text-decoration: none;
  font-weight: normal;
  white-space: nowrap;
  margin: 0 10px;
  font-size: 1.6em;
  padding: 20px 10px;
  background-color: ${(props) => (props.isActive() ? "#524c46" : "#3e3832")};
  &:hover {
    background-color: #48423c;
  }
`;

type Props = {
  to: string;
  children: string;
  isActive: (pathename: string) => boolean;
};

export default ({ to, children, isActive }: Props) => {
  const { pathname } = useLocation();
  return (
    <HeaderLink to={to} isActive={() => isActive(pathname)}>
      {children}
    </HeaderLink>
  );
};
