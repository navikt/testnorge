// Import all CSS først
import './styles/main.less'
import '~/utils/FormatIso'
import './polyfill'

import React from 'react'
import { render } from 'react-dom'
import Logger from './logger'
import { v4 as uuid } from 'uuid'
import { RootComponent } from '~/RootComponent'

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

render(<RootComponent />, root)
