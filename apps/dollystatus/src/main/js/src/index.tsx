import { createRoot } from 'react-dom/client'
import React from 'react'
import './index.less'
import App from './App'

const container = document.getElementById('root')

if (!container) {
	throw new Error('Fant ikke rotnoden for applikasjonen')
}

const enableMocking = async () => {
	if (import.meta.env.MODE !== 'mock') {
		return
	}
	const { worker } = await import('@/mocks/browser')
	await worker.start({ onUnhandledRequest: 'bypass' })
}

enableMocking().then(() => createRoot(container).render(<App />))
