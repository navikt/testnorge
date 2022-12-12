// Import all CSS først
import '@navikt/ds-css'
import '@/styles/main.less'
import '@/utils/FormatIso'
import '@/polyfill'

import { v4 as uuid } from 'uuid'
import { RootComponent } from '@/RootComponent'
import { Logger } from '@/logger/Logger'
import { createRoot } from 'react-dom/client'

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

const container = document.getElementById('root')
const root = createRoot(container)

root.render(<RootComponent />)
