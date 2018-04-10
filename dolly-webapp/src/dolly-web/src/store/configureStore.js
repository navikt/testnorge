import {createStore, combineReducers, applyMiddleware} from 'redux';
import reducerCollection from '../reducers';
import reduxImmutableStateInvariant from 'redux-immutable-state-invariant';
import thunk from 'redux-thunk';
import {routerReducer, routerMiddleware } from 'react-router-redux';



//TODO flytt config av store inn hit senere.
/*
export default function getConfiguredStore(history){

    const middleware = routerMiddleware(history);

    return createStore(
        combineReducers({
            ...reducerCollection,
            router: routerReducer
        }),
        applyMiddleware(middleware, thunk, reduxImmutableStateInvariant())
    );
}
*/

