import { createRoot } from 'react-dom/client'
import React from 'react'
import './index.less'
import App from './App'

const container = document.getElementById('root')

if (!container) {
	throw new Error('Fant ikke rotnoden for applikasjonen')
}

const root = createRoot(container)

root.render(<App />)
