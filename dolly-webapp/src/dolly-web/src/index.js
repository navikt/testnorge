import React from 'react';
import AppFrame from './AppFrame';
import ReactDOM from 'react-dom';
import createHistory from 'history/createBrowserHistory';
import {Provider} from 'react-redux';
import {ConnectedRouter} from 'react-router-redux';
import Routes from './Routes';
import registerServiceWorker from './registerServiceWorker';
import configureStore from './store/configureStore';
import 'normalize.css';
import {fetchGrupper} from "./actions/gruppeActions";


const history = createHistory();

const appReduxStore = configureStore(history);

appReduxStore.dispatch(fetchGrupper());

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
