import 'babel-polyfill';
import React from 'react';
import ReactDOM from 'react-dom';
import createHistory from 'history/createBrowserHistory';
import registerServiceWorker from './registerServiceWorker';
import configureStore from './store/configureStore';
import 'normalize.css';
import './index.css';
import {Provider} from 'react-redux';
import {ConnectedRouter} from 'react-router-redux';
import App from './App';
import {AppContainer} from 'react-hot-loader';
import {fetchGrupper} from "./actions/gruppeActions";
import {fetchTeams} from "./actions/teamActions";

const history = createHistory();

const appReduxStore = configureStore(history);

appReduxStore.dispatch(fetchGrupper());
appReduxStore.dispatch(fetchTeams());

const render = Component => {
    ReactDOM.render((
            <AppContainer>
                <Provider store={appReduxStore}>
                    <ConnectedRouter history={history}>
                        <App/>
                    </ConnectedRouter>
                </Provider>
            </AppContainer>
        ),
        document.getElementById('root'));
};

registerServiceWorker();

render(App);

if (module.hot) {
    module.hot.accept('./App', () => {
        render(App);
    });
}



