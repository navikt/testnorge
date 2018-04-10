import {createStore, combineReducers, applyMiddleware} from 'redux';
import reducerCollection from '../reducers';
import reduxImmutableStateInvariant from 'redux-immutable-state-invariant';
import thunk from 'redux-thunk';
import {routerReducer, routerMiddleware} from 'react-router-redux';

// Redux store config.
const appReduxStoreConfig = (history) => {

    const middleware = routerMiddleware(history);

    return createStore(
        combineReducers({
            ...reducerCollection,
            router: routerReducer
        }),
        applyMiddleware(middleware, thunk, reduxImmutableStateInvariant())
    );
};

export default appReduxStoreConfig;
