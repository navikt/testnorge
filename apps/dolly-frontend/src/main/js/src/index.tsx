import React from 'react'
import * as ReactDOM from 'react-dom/client'

// Import all CSS først
import '@navikt/ds-css'
import '@/styles/main.less'
import { RootComponent } from '@/RootComponent'
import { runningE2ETest } from '@/service/services/Request'

async function enableMocking() {
	if (process.env.NODE_ENV !== 'test' || runningE2ETest()) {
		return
	}

	const { worker } = await import('../__tests__/mocks/browser')

	return worker.start()
}

enableMocking().then(() => {
	const root = ReactDOM.createRoot(document.getElementById('root'))

	root.render(
		<React.StrictMode>
			<RootComponent />
		</React.StrictMode>,
	)
})
