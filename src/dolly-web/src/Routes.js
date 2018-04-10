import React from 'react';
import {Route} from 'react-router-dom';
import {Switch} from 'react-router-dom';
import Home from './components/home/Home';

const Routes = () => (
    <Switch>
        <Route path="/" component={Home} />
    </Switch>
);

export default Routes;
