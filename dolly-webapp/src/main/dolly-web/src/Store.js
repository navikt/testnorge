import { createStore, combineReducers, applyMiddleware } from 'redux'
import thunkMiddleware from 'redux-thunk'
import { connectRouter, routerMiddleware } from 'connected-react-router'
import { composeWithDevTools } from 'redux-devtools-extension/developmentOnly'

import bestillingReducer from './ducks/bestilling'
import grupperReducer from './ducks/grupper'
import gruppeReducer from './ducks/gruppe'
import teamsReducer from './ducks/teams'
import teamReducer from './ducks/team'
import brukerReducer from './ducks/bruker'
import testbrukerReducer from './ducks/testBruker'
import searchReducer from './ducks/search'

export default function configureReduxStore(history) {
	const allMiddleware = [thunkMiddleware, routerMiddleware(history)]

	// Add redux logger if not in production
	if (process.env.NODE_ENV !== `production`) {
		const createLogger = require(`redux-logger`).createLogger
		const logger = createLogger({ collapsed: true })
		allMiddleware.push(logger)
	}

	const rootReducer = combineReducers({
		bestilling: bestillingReducer,
		grupper: grupperReducer,
		gruppe: gruppeReducer,
		teams: teamsReducer,
		team: teamReducer,
		bruker: brukerReducer,
		testbruker: testbrukerReducer,
		search: searchReducer
	})

	return createStore(
		connectRouter(history)(rootReducer),
		composeWithDevTools(applyMiddleware(...allMiddleware))
	)
}
