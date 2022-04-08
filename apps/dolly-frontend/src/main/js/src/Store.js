import { applyMiddleware, combineReducers, createStore } from 'redux'
import thunkMiddleware from 'redux-thunk'
import { createPromise } from 'redux-promise-middleware'
import { composeWithDevTools } from 'redux-devtools-extension/developmentOnly'
import gruppeReducer from './ducks/gruppe'
import brukerReducer from './ducks/bruker'
import fagsystemReducer from './ducks/fagsystem'
import searchReducer from './ducks/search'
import loadingReducer from './ducks/loading'
import errorsReducer from './ducks/errors'
import bestillingReducer from './ducks/bestilling'
import bestillingStatusReducer from './ducks/bestillingStatus'
import environmentsReducer from './ducks/environments'
import kodeverkReducer from './ducks/kodeverk'
import varslingerReducer from './ducks/varslinger'
import finnPersonReducer from './ducks/finnPerson'
import organisasjonReducer from './ducks/organisasjon'
import commonReducer from '~/ducks/common'
import { createReduxHistoryContext, LOCATION_CHANGE } from 'redux-first-history'
import { createBrowserHistory } from 'history'

const locationMiddleware = (store) => (next) => (action) => {
	if (action.type === LOCATION_CHANGE) {
		const prevPath = store.getState()?.router?.location?.pathname
		const nextPath = action.payload.location.pathname
		if (prevPath === nextPath) {
			return false
		}
	}
	return next(action)
}

const { createReduxHistory, routerMiddleware, routerReducer } = createReduxHistoryContext({
	history: createBrowserHistory(),
})

const allMiddleware = [
	routerMiddleware,
	locationMiddleware,
	thunkMiddleware,
	createPromise({ promiseTypeSuffixes: ['REQUEST', 'SUCCESS', 'FAILURE'] }),
]

const rootReducer = () =>
	combineReducers({
		router: routerReducer,
		bestveil: bestillingReducer,
		bestillingStatuser: bestillingStatusReducer,
		gruppe: gruppeReducer,
		bruker: brukerReducer,
		fagsystem: fagsystemReducer,
		search: searchReducer,
		loading: loadingReducer,
		errors: errorsReducer,
		common: commonReducer,
		environments: environmentsReducer,
		kodeverk: kodeverkReducer,
		varslinger: varslingerReducer,
		finnPerson: finnPersonReducer,
		organisasjon: organisasjonReducer,
	})

export const store = createStore(
	rootReducer(),
	composeWithDevTools(applyMiddleware(...allMiddleware))
)

export const history = createReduxHistory(store)
