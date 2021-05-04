import React, { useState } from "react";

import Navigation from "@/components/Navigation";
import "./PageWithMenu.less";

type Props = {
  children: React.ReactNode;
  navigations: Navigation[];
  menuTitle: string;
};

export default ({ children, navigations, menuTitle }: Props) => {
  const [search, setSearch] = useState("");

  return (
    <div className="page-with-menu">
      <div className="container--left">
        <h4>Søk etter applikasjon</h4>
        <input
          type="text"
          autoFocus
          className="search"
          onChange={(event) => setSearch(event.target.value)}
        />
        <h3>{menuTitle}</h3>
        <ul>
          {navigations
            .filter((name) => name.label.includes(search))
            .sort((first, second) => first.label.localeCompare(second.label))
            .map((navigation) => (
              <li key={navigation.label}>
                <Navigation navigation={navigation} />
              </li>
            ))}
        </ul>
      </div>
      <div className="container--right">{children}</div>
    </div>
  );
};
