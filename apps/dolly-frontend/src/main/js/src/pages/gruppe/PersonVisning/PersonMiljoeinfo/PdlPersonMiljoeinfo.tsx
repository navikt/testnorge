import React from 'react'
import Loading from '~/components/ui/loading/Loading'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { PdlDataVisning } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataVisning'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

interface PdlMiljoeValues {
	data: any
	loading: boolean
}

export const PdlPersonMiljoeInfo = ({ data, loading }: PdlMiljoeValues) => {
	if (!data && !loading) return null

	return (
		<ErrorBoundary>
			<div>
				<SubOverskrift label="PDL" iconKind="visTpsData" />
				{loading && <Loading label="Henter info fra PDL" />}
				{/* @ts-ignore */}
				{data && <PdlDataVisning data={data.data} />}
				{data && (
					<p>
						<i>Hold pekeren over PDL for å se dataene som finnes på denne personen i PDL</i>
					</p>
				)}
			</div>
		</ErrorBoundary>
	)
}
