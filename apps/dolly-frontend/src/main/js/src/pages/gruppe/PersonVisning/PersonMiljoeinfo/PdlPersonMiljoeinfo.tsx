import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { PdlDataVisning } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataVisning'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

interface PdlMiljoeValues {
	ident: string
}

export const PdlPersonMiljoeInfo = ({ ident }: PdlMiljoeValues) => {
	return (
		<ErrorBoundary>
			<div>
				<SubOverskrift label="PDL" iconKind="visTpsData" />
				<PdlDataVisning ident={ident} />
				<p>
					<i>Hold pekeren over PDL for å se dataene som finnes på denne personen i PDL</i>
				</p>
			</div>
		</ErrorBoundary>
	)
}
