import React from 'react'
import * as ReactDOM from 'react-dom/client'

// Import all CSS først
import '@navikt/ds-css'
import '@/styles/main.less'
import '@/utils/FormatIso'
import { RootComponent } from '@/RootComponent'

const root = ReactDOM.createRoot(document.getElementById('root'))

root.render(<RootComponent />)
