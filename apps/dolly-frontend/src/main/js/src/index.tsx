import React from 'react'
import * as ReactDOM from 'react-dom/client'

// Import all CSS først
import '@navikt/ds-css'
import '@/styles/main.less'
import { RootComponent } from '@/RootComponent'

async function enableMocking() {
	if (process.env.NODE_ENV !== 'development') {
		return
	}

	const { worker } = await import('../__tests__/mocks/browser')

	return worker.start()
}

enableMocking().then(() => {
	const root = ReactDOM.createRoot(document.getElementById('root'))

	root.render(<RootComponent />)
})
