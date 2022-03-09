import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Loading from '~/components/ui/loading/Loading'
import { Historikk } from '~/components/ui/historikk/Historikk'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

type NomVisningProps = {
	data: Array<Data>
	loading: boolean
}

type VisningProps = {
	data: Data
}

type Data = {
	opprettRessurs: {
		navIdent: string
	}
}

export const Visning = ({ data }: VisningProps) => {
	return (
		<>
			<TitleValue title="Navident" value={data.opprettRessurs?.navIdent} />
		</>
	)
}

export const NomVisning = ({ data, loading }: NomVisningProps) => {
	if (loading) return <Loading label="Laster Nom data" />
	if (!data) return false

	const sortedData = Array.isArray(data) ? data.slice().reverse() : data

	return (
		<ErrorBoundary>
			<div>
				<SubOverskrift label="NAV Organisasjonsmaster" iconKind="organisasjon" />
				<div className="person-visning_content">
					<Historikk component={Visning} data={sortedData} />
				</div>
			</div>
		</ErrorBoundary>
	)
}
