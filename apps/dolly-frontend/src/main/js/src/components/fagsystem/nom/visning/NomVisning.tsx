import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Loading from '~/components/ui/loading/Loading'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { addDays, isBefore } from 'date-fns'
import Formatters from '~/utils/DataFormatter'

type NomVisningProps = {
	data: Data
	loading: boolean
	skjermingsregister: Skjerming
}

type VisningProps = {
	data: Data
	skjermingsregister: Skjerming
}

type Skjerming = {
	skjermetFra: Date
	skjermetTil: Date
}

type Data = {
	ressurs: {
		navIdent: string
	}
}

export const Visning = ({ data, skjermingsregister }: VisningProps) => (
	<>
		<TitleValue title="Nav ident" value={data?.ressurs?.navIdent} />
		{skjermingsregister && (
			<>
				<TitleValue
					title="Har skjerming"
					value={
						skjermingsregister?.skjermetTil &&
						isBefore(new Date(skjermingsregister.skjermetTil), addDays(new Date(), -1))
							? 'NEI'
							: 'JA'
					}
				/>
				<TitleValue
					title="Skjerming fra"
					value={Formatters.formatDate(skjermingsregister?.skjermetFra)}
				/>
				<TitleValue
					title="Skjerming til"
					value={Formatters.formatDate(skjermingsregister?.skjermetTil)}
				/>
			</>
		)}
	</>
)

export const NomVisning = ({ data, skjermingsregister, loading }: NomVisningProps) => {
	if (loading) return <Loading label="Laster Nom data" />
	if (!data && !skjermingsregister) return false

	return (
		<ErrorBoundary>
			<div>
				<SubOverskrift label="NAV ansatt" iconKind="organisasjon" />
				<div className="person-visning_content">
					<Visning data={data} skjermingsregister={skjermingsregister} />
				</div>
			</div>
		</ErrorBoundary>
	)
}
