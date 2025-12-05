import { combineReducers } from 'redux'
import { thunk } from 'redux-thunk'
import { createPromise } from 'redux-promise-middleware'
import gruppeReducer from '@/ducks/gruppe'
import fagsystemReducer from '@/ducks/fagsystem'
import searchReducer from '@/ducks/search'
import loadingReducer from '@/ducks/loading'
import errorsReducer from '@/ducks/errors'
import bestillingReducer from '@/ducks/bestilling'
import kodeverkReducer from '@/ducks/kodeverk'
import varslingerReducer from '@/ducks/varslinger'
import finnPersonReducer from '@/ducks/finnPerson'
import commonReducer from '@/ducks/common'
import { createReduxHistoryContext, LOCATION_CHANGE } from 'redux-first-history'
import { createBrowserHistory } from 'history'
import { configureStore, Tuple } from '@reduxjs/toolkit'

const locationMiddleware = (reduxStore) => (next) => (action) => {
	if (action.type === LOCATION_CHANGE) {
		const prevPath = reduxStore.getState()?.router?.location?.pathname
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
	thunk,
	createPromise({ promiseTypeSuffixes: ['REQUEST', 'SUCCESS', 'FAILURE'] }),
]

const rootReducer = () =>
	combineReducers({
		router: routerReducer,
		bestveil: bestillingReducer,
		gruppe: gruppeReducer,
		fagsystem: fagsystemReducer,
		search: searchReducer,
		loading: loadingReducer,
		errors: errorsReducer,
		common: commonReducer,
		kodeverk: kodeverkReducer,
		varslinger: varslingerReducer,
		finnPerson: finnPersonReducer,
	})

export const store = configureStore({
	reducer: rootReducer(),
	middleware: () => new Tuple(...allMiddleware),
})

// Infer the `RootState` and `AppDispatch` types from the store itself
export type RootState = ReturnType<typeof store.getState>
// Inferred type: {posts: PostsState, comments: CommentsState, users: UsersState}
export type AppDispatch = typeof store.dispatch

export const history = createReduxHistory(store)
