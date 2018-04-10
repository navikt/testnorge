import React from 'react';
import {Provider} from 'react-redux';
import ReactDOM from 'react-dom';
import {ConnectedRouter} from 'react-router-redux';
import createHistory from 'history/createBrowserHistory';
import Routes from './Routes';
import registerServiceWorker from './registerServiceWorker';
import AppFrame from './AppFrame';
import {fetchPersons} from "./actions/personActions";
import 'normalize.css';
import {createStore, combineReducers, applyMiddleware} from 'redux';
import reducerCollection from './reducers';
import reduxImmutableStateInvariant from 'redux-immutable-state-invariant';
import thunk from 'redux-thunk';
import {routerReducer, routerMiddleware } from 'react-router-redux';


const history = createHistory();
const middleware = routerMiddleware(history);

// Redux store config.
const appReduxStore = createStore(
    combineReducers({
        ...reducerCollection,
        router: routerReducer
    }),
    applyMiddleware(middleware, thunk, reduxImmutableStateInvariant())
);

appReduxStore.dispatch(fetchPersons());

ReactDOM.render((
    <Provider store={appReduxStore}>
        <ConnectedRouter history={history}>
            <AppFrame>
                <Routes/>
            </AppFrame>
        </ConnectedRouter>
    </Provider>
    ),
    document.getElementById('root'));


registerServiceWorker();
