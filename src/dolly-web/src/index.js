import 'babel-polyfill';
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
import './index.css';
import {fetchGrupper} from "./actions/gruppeActions";
import {fetchTeams} from "./actions/teamActions";
import {AppContainer} from 'react-hot-loader';

const history = createHistory();

const appReduxStore = configureStore(history);

appReduxStore.dispatch(fetchGrupper());
appReduxStore.dispatch(fetchTeams());

const App = () => {
    return (
        <Provider store={appReduxStore}>
            <ConnectedRouter history={history}>
                    <AppFrame>
                        <Routes/>
                    </AppFrame>
            </ConnectedRouter>
        </Provider>
    )
};

const render = Component => {
    ReactDOM.render((
            <AppContainer>
                <App/>
            </AppContainer>
        ),
        document.getElementById('root'));
};

registerServiceWorker();

render(App);

if (module.hot) {
    module.hot.accept('./', () => {
        render(App);
    });
}



