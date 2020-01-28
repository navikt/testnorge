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
