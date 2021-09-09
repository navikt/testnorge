// Import all CSS først
import './styles/main.less'
import '~/utils/FormatIso'
import './polyfill'

import React from 'react'
import { render } from 'react-dom'
import { Provider } from 'react-redux'
import store from './Store'
import { ConnectedRouter } from 'connected-react-router'
import AppConnector from './app/AppConnector'
import history from './history'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Logger from './logger'
import { v4 as uuid } from 'uuid'

window.uuid = uuid()
window.onerror = (message) => {
	try {
		Logger.error({
			event: 'Global feil',
			message: message,
			uuid: window.uuid,
		})
	} catch (e) {
		console.error(e)
	}
}

const root = document.getElementById('root')

render(
	<Provider store={store}>
		<ConnectedRouter history={history}>
			<ErrorBoundary>
				<AppConnector />
			</ErrorBoundary>
		</ConnectedRouter>
	</Provider>,
	root
)
