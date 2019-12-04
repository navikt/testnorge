import { createStore, combineReducers, applyMiddleware } from 'redux'
import thunkMiddleware from 'redux-thunk'
import { createPromise } from 'redux-promise-middleware'
import { connectRouter, routerMiddleware, LOCATION_CHANGE } from 'connected-react-router'
import { composeWithDevTools } from 'redux-devtools-extension/developmentOnly'
import gruppeReducer from './ducks/gruppe'
import brukerReducer from './ducks/bruker'
import fagsystemReducer from './ducks/fagsystem'
import searchReducer from './ducks/search'
import loadingReducer from './ducks/loading'
import errorsReducer from './ducks/errors'
import commonReducer from './ducks/common'
import bestillingStatusReducer from './ducks/bestillingStatus'
import environmentsReducer from './ducks/environments'
import kodeverkReducer from './ducks/kodeverk'
import history from './history'

const locationMiddleware = store => next => action => {
	if (action.type === LOCATION_CHANGE) {
		const prevPath = store.getState().router.location.pathname
		const nextPath = action.payload.location.pathname
		if (prevPath === nextPath) {
			return false
		}
	}
	return next(action)
}

const configureReduxStore = history => {
	const allMiddleware = [
		locationMiddleware,
		thunkMiddleware,
		createPromise({ promiseTypeSuffixes: ['REQUEST', 'SUCCESS', 'FAILURE'] }),
		routerMiddleware(history)
	]

	const rootReducer = history =>
		combineReducers({
			router: connectRouter(history),
			bestillingStatuser: bestillingStatusReducer,
			gruppe: gruppeReducer,
			bruker: brukerReducer,
			fagsystem: fagsystemReducer,
			search: searchReducer,
			loading: loadingReducer,
			errors: errorsReducer,
			common: commonReducer,
			environments: environmentsReducer,
			kodeverk: kodeverkReducer
		})

	return createStore(rootReducer(history), composeWithDevTools(applyMiddleware(...allMiddleware)))
}

export default configureReduxStore(history)
