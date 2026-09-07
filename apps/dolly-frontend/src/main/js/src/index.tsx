import React from 'react'
import * as ReactDOM from 'react-dom/client'

// Import all CSS først
import '@navikt/ds-css'
import '@/styles/main.less'
import { RootComponent } from '@/RootComponent'
import { runningE2ETest } from '@/service/services/Request'

async function enableMocking() {
	if (import.meta.env.MODE !== 'test' || runningE2ETest()) {
		return
	}

	const { worker } = await import('../__tests__/mocks/browser')

	return worker.start()
}

enableMocking().then(() => {
	const rootElement = document.getElementById('root')
	if (!rootElement) {
		throw new Error('Fant ikke rot-elementet for Dolly')
	}

	const root = ReactDOM.createRoot(rootElement)

	root.render(
		<React.StrictMode>
			<RootComponent />
		</React.StrictMode>,
	)
})
