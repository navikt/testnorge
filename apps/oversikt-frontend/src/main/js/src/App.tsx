import React, { useEffect, useState } from "react";
import "./App.less";
// @ts-ignore
import { fetchApplications } from "./api";
import Header from "@/components/Header";
import NavigationBar from "@/components/NavigationBar";

import { HashRouter as Router, Route, Switch } from "react-router-dom";
import MagicTokenPage from "@/pages/MagicTokenPage";
import AccessTokenPage from "@/pages/AccessTokenPage";
import NavFrontendSpinner from "nav-frontend-spinner";
import SessionTimer from "@/components/SessionTimer";

function App() {
  const [items, setItems] = useState<string[] | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    fetchApplications().then((response: string[]) => {
      setItems(response);
      setLoading(false);
    });
  }, []);

  return (
    <div className="app">
      <Header />
      <SessionTimer />
      {loading ? (
        <NavFrontendSpinner />
      ) : (
        <Router>
          <NavigationBar
            navigations={[
              {
                href: "/",
                label: "Magic Token",
              },
              {
                href: "/access-token/" + items[0],
                label: "Access token",
              },
            ]}
          />
          <Switch>
            <Route path="/access-token/:name">
              <AccessTokenPage
                navigations={items.map((item: string) => ({
                  href: "/access-token/" + item,
                  label: item,
                }))}
              />
            </Route>
            <Route path="/">
              <MagicTokenPage />
            </Route>
          </Switch>
        </Router>
      )}
    </div>
  );
}

export default App;
