import React from 'react';
import ReactDOM from 'react-dom';
import AppFrame from './AppFrame';
import createHistory from 'history/createBrowserHistory';
import {Provider} from 'react-redux';
import {ConnectedRouter} from 'react-router-redux';
import Routes from './Routes';
import registerServiceWorker from './registerServiceWorker';
import {fetchPersons} from "./actions/personActions";
import configureStore from './store/configureStore';
import 'normalize.css';


const history = createHistory();

const appReduxStore = configureStore(history);

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
