import React, {useState, useEffect} from 'react';
import './App.css';
import {Frame} from "./components/Frame";
import {BrowserRouter as Router, Route, Switch} from "react-router-dom";
import Page from "./components/Page";
import Menu from "./components/Menu";
import {fetchApplications} from "./api";


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
                                <Page/>
                            </Route>
                        </Switch>
                    </Router>
                )}
            />
        </div>
    )
}

export default App;
