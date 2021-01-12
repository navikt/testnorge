import React from "react";
import "./Header.less";
import Logo from "@/assets/Logo";

const Header = () => (
  <div className="header">
    <Logo className="header__logo" />
    <h1>Testnorge oversikt</h1>
  </div>
);

export default Header;
