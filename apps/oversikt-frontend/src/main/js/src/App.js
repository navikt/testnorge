import React, {useEffect, useState} from 'react';
import './App.css';
import {Frame} from "./components/Frame";
import {BrowserRouter as Router, Route, Switch} from "react-router-dom";
import AccessTokenPage from "./components/AccessTokenPage";
import Menu from "./components/Menu";
import {fetchApplications} from "./api";
import {Page} from "./components/Page/Page";


function App() {
    const [items, setItems] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchApplications().then(response => {
            setItems(response)
            setLoading(false)
        })
    }, []);

    return (
        <div className="App">
            <Frame
                renderLeft={() => loading ? <p>Loading...</p> : <Menu items={items}/>}
                renderRight={() => (
                    <Router>
                        <Switch>
                            <Route path="/app/:name">
                                <AccessTokenPage/>
                            </Route>
                            <Route path="/">
                                <Page
                                    title={"Velg en applikasjon"}
                                    paragraph="For å kunne teste OnBehalfOf-flow fra Azure AD må applikasjonene ha en frontend."
                                />
                            </Route>
                        </Switch>
                    </Router>
                )}
            />
        </div>
    )
}

export default App;
