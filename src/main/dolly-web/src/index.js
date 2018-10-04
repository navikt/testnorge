// Import all CSS først
import './styles/main.less'

import React from 'react'
import { render } from 'react-dom'
import { Provider } from 'react-redux'
import store from './Store'
import { ConnectedRouter } from 'connected-react-router'
import AppConnector from './app/AppConnector'
import history from './history'

/*
 * Fontene må legges inn til slutt for å override fonter allerede inkludert i NAV pakker. 
 * De fontene som er inkludert i NAV pakker er BASE64 encodet, fontene ser helt annerledes
 * ut når de rendres og blir veldig pixelated. (spesielt synlig på bold font)
 */
import './styles/fonts.less'

render(
	<Provider store={store}>
		<ConnectedRouter history={history}>
			<AppConnector />
		</ConnectedRouter>
	</Provider>,
	document.getElementById('root')
)
