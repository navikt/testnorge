import 'babel-polyfill'
import React from 'react'
import { render } from 'react-dom'
import { Provider } from 'react-redux'
import createHistory from 'history/createBrowserHistory'
import configureStore from './Store'
import './styles/main.less'
import { ConnectedRouter } from 'react-router-redux'
import App from './app/App'
import { getGrupper } from './ducks/grupper'
import { fetchTeams } from './ducks/team'

const history = createHistory()

const appReduxStore = configureStore(history)

appReduxStore.dispatch(getGrupper())
appReduxStore.dispatch(fetchTeams())

render(
	<Provider store={appReduxStore}>
		<ConnectedRouter history={history}>
			<App />
		</ConnectedRouter>
	</Provider>,
	document.getElementById('root')
)
