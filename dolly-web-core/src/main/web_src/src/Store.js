import { createStore, combineReducers, applyMiddleware } from 'redux'
import thunkMiddleware from 'redux-thunk'
import promiseMiddleware from 'redux-promise-middleware'
import { connectRouter, routerMiddleware, LOCATION_CHANGE } from 'connected-react-router'
import { composeWithDevTools } from 'redux-devtools-extension/developmentOnly'
import bestillingReducer from './ducks/bestilling'
import gruppeReducer from './ducks/gruppe'
import teamsReducer from './ducks/teams'
import brukerReducer from './ducks/bruker'
import testbrukerReducer from './ducks/testBruker'
import searchReducer from './ducks/search'
import loadingReducer from './ducks/loading'
import errorsReducer from './ducks/errors'
import commonReducer from './ducks/common'
import bestillingStatusReducer from './ducks/bestillingStatus'
import environmentsReducer from './ducks/environments'
import oppslagReducer from './ducks/oppslag'
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
		promiseMiddleware({ promiseTypeSuffixes: ['REQUEST', 'SUCCESS', 'FAILURE'] }),
		routerMiddleware(history)
	]

	// * Trenger ikke denne hvis du foretrekker redux chrome-extension.
	// addReduxLoggerToConsole(allMiddleware)

	const rootReducer = history =>
		combineReducers({
			router: connectRouter(history),
			currentBestilling: bestillingReducer,
			bestillingStatuser: bestillingStatusReducer,
			gruppe: gruppeReducer,
			teams: teamsReducer,
			bruker: brukerReducer,
			testbruker: testbrukerReducer,
			search: searchReducer,
			loading: loadingReducer,
			errors: errorsReducer,
			common: commonReducer,
			environments: environmentsReducer,
			oppslag: oppslagReducer
		})

	return createStore(rootReducer(history), composeWithDevTools(applyMiddleware(...allMiddleware)))
}

const addReduxLoggerToConsole = allMiddleware => {
	if (process.env.NODE_ENV !== `production`) {
		const createLogger = require(`redux-logger`).createLogger
		const logger = createLogger({ collapsed: true })
		allMiddleware.push(logger)
	}
}

export default configureReduxStore(history)
