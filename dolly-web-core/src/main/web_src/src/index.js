// Import all CSS først
import './styles/main.less'
import '~/utils/FormatIso'

import React from 'react'
import { render } from 'react-dom'
import { Provider } from 'react-redux'
import store from './Store'
import { ConnectedRouter } from 'connected-react-router'
import AppConnector from './app/AppConnector'
import history from './history'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Logger from './logger'
import {v4 as uuid} from 'uuid';

window.uuid = uuid();
window.onerror = (message) => {
	try {
		Logger.error("Global feil", message, window.uuid)
	} catch (e) {
		console.error(e);
	}
};

render(
	<Provider store={store}>
		<ConnectedRouter history={history}>
			<ErrorBoundary error="React:GlobalErrorBoundary">
				<AppConnector />
			</ErrorBoundary>
		</ConnectedRouter>
	</Provider>,
	document.getElementById('root')
)
