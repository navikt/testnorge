import React from 'react'
import cn from 'classnames'
import Etikett, { EtikettBaseProps } from 'nav-frontend-etiketter'

import './etikett.less'

interface ImportFraEtikettProps {
	importFra: string
	type: EtikettBaseProps
	venstreMargin?: boolean
}

const importType = (fra: string) => {
	if (fra === 'TPS') return 'Mini-Norge'
	else if (fra === 'PDL' || fra === 'PDLF') return 'PDL'
	else return 'Ukjent'
}

const ImportFraEtikett = ({ importFra, type, venstreMargin }: ImportFraEtikettProps) => {
	if (!importFra) return null

	const css = cn('dolly-etikett', { 'dolly-etikett--venstre-margin': venstreMargin })
	return (
		<Etikett mini className={css} type={type} title={`Importert fra ${importType(importFra)}`}>
			{importType(importFra)}
		</Etikett>
	)
}

export { ImportFraEtikett }
