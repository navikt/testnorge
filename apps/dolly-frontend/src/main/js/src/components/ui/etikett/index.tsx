import React from 'react'
import cn from 'classnames'

import './etikett.less'
import { Tag } from '@navikt/ds-react'

interface ImportFraEtikettProps {
	importFra: string
	variant: any
	venstreMargin?: boolean
}

const importType = (fra: string) => {
	if (fra === 'TPS') return 'Mini-Norge'
	else if (fra === 'PDL' || fra === 'PDLF') return 'PDL'
	else if (fra === 'Test-Norge') return 'Test-Norge'
	else return 'Ukjent'
}

const ImportFraEtikett = ({ importFra, variant, venstreMargin }: ImportFraEtikettProps) => {
	if (!importFra) return null

	const css = cn('dolly-etikett', { 'dolly-etikett--venstre-margin': venstreMargin })
	return (
		<Tag variant={variant} className={css} title={`Importert fra ${importType(importFra)}`}>
			{importType(importFra)}
		</Tag>
	)
}

export { ImportFraEtikett }
