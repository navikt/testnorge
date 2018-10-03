import React from 'react'
import { render } from 'react-dom'
import { Provider } from 'react-redux'
import store from './Store'
import { ConnectedRouter } from 'connected-react-router'
import AppConnector from './app/AppConnector'
import history from './history'

import './styles/main.less'

render(
	<Provider store={store}>
		<ConnectedRouter history={history}>
			<AppConnector />
		</ConnectedRouter>
	</Provider>,
	document.getElementById('root')
)
