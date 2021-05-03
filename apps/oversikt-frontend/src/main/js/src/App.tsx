import React from "react";
import "./App.less";
// @ts-ignore
import ApplicationService from "@/services/ApplicationService";

import { HashRouter as Router, Route, Switch } from "react-router-dom";
import MagicTokenPage from "@/pages/MagicTokenPage";
import AccessTokenPage from "@/pages/AccessTokenPage";
import {
  Header,
  HeaderLink,
  HeaderLinkGroup,
  LoadableComponent,
} from "@navikt/dolly-komponenter";
import { ProfilLoader } from "@/components/profil";

export default () => {
  return (
    <Router>
      <Header title="Generer token" profile={<ProfilLoader />}>
        <HeaderLinkGroup>
          <HeaderLink
            href="#/magic-token"
            isActive={() =>
              window.location.hash === "/" ||
              window.location.hash.includes("/magic-token")
            }
          >
            Magic Token
          </HeaderLink>
          <HeaderLink
            href="#/access-token/dolly-backend"
            isActive={() => window.location.hash.includes("/access-token")}
          >
            Access Token
          </HeaderLink>
        </HeaderLinkGroup>
      </Header>
      <div className="body">
        <Switch>
          <Route path="/access-token/:name">
            <LoadableComponent
              onFetch={ApplicationService.fetchApplications}
              render={(items) => (
                <AccessTokenPage
                  navigations={items.map((item: string) => ({
                    href: "/access-token/" + item,
                    label: item,
                  }))}
                />
              )}
            />
          </Route>
          <Route path="/">
            <MagicTokenPage />
          </Route>
        </Switch>
      </div>
    </Router>
  );
};
