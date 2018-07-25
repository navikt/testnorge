import { createStore, combineReducers, applyMiddleware } from 'redux'
import thunkMiddleware from 'redux-thunk'
import { connectRouter, routerMiddleware } from 'connected-react-router'
import { composeWithDevTools } from 'redux-devtools-extension/developmentOnly'

import gruppeReducer from './ducks/grupper'
import teamReducer from './ducks/team'
import brukerReducer from './ducks/bruker'
import testbrukerReducer from './ducks/testBruker'

export default function configureReduxStore(history) {
	const allMiddleware = [thunkMiddleware, routerMiddleware(history)]

	// Add redux logger if not in production
	if (process.env.NODE_ENV !== `production`) {
		const createLogger = require(`redux-logger`).createLogger
		const logger = createLogger({ collapsed: true })
		allMiddleware.push(logger)
	}

	const rootReducer = combineReducers({
		grupper: gruppeReducer,
		team: teamReducer,
		bruker: brukerReducer,
		testbruker: testbrukerReducer
	})

	return createStore(
		connectRouter(history)(rootReducer),
		composeWithDevTools(applyMiddleware(...allMiddleware))
	)
}
