import React from 'react'
import cn from 'classnames'
import Etikett, { EtikettBaseProps } from 'nav-frontend-etiketter'

import './etikett.less'

interface ImportFraEtikett {
	importFra: string
	type: EtikettBaseProps
	venstreMargin?: boolean
}

const importType = (fra: string) => {
	if (fra === 'TPS') return 'Mini-Norge'
	else return 'Ukjent'
}

const ImportFraEtikett = ({ importFra, type, venstreMargin }: ImportFraEtikett) => {
	if (!importFra) return null

	const css = cn('dolly-etikett', { 'dolly-etikett--venstre-margin': venstreMargin })
	return (
		<Etikett mini className={css} type={type} title={`Importert fra ${importType(importFra)}`}>
			{importType(importFra)}
		</Etikett>
	)
}

export { ImportFraEtikett }
