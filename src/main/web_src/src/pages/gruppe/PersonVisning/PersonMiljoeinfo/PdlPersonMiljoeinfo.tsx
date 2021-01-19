import React from 'react'
import { useAsync } from 'react-use'
import { DollyApi } from '~/service/Api'
import Loading from '~/components/ui/loading/Loading'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { PdlDataVisning } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataVisning'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

type PdlPersonMiljoeinfo = {
	ident: string
}

export const PdlPersonMiljoeInfo = ({ ident }: PdlPersonMiljoeinfo) => {
	if (!ident) return null

	const state = useAsync(async () => {
		const response = await DollyApi.getPersonFraPdl(ident)
		return response.data
	}, [])

	return (
		<ErrorBoundary>
			<div>
				<SubOverskrift label="PDL" iconKind="visTpsData" />
				{state.loading && <Loading label="Henter info fra PDL" />}
				{/* @ts-ignore */}
				{state.value && <PdlDataVisning data={state.value.data} />}
				{state.value && (
					<p>
						<i>Hold pekeren over PDL for å se dataene som finnes på denne personen i PDL</i>
					</p>
				)}
			</div>
		</ErrorBoundary>
	)
}
