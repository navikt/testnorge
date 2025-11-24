import { setupWorker } from 'msw/browser'
import { handlers } from './handlers'

import '@navikt/ds-css'
import '@/styles/main.less'

export const worker = setupWorker(...handlers) as any
