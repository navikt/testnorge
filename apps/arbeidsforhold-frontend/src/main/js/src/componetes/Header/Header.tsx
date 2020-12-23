import React from "react";
import "./Header.less";
import Logo from "@/assets/Logo";

const Header = () => (
  <div className="header">
    <Logo className="header__logo" />
    <h1>Arbeidsforhold A-meldinger viewer</h1>
  </div>
);

export default Header;
