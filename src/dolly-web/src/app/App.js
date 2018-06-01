import 'babel-polyfill'
import React from 'react'
import AppFrame from './AppFrame'
import Routes from '../Routes'

const App = () => {
	return (
		<AppFrame>
			<Routes />
		</AppFrame>
	)
}

export default App
