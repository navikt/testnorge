import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

type Data = {
	data: FullmaktData
}

type FullmaktData = {
	gyldigFraOgMed: Date
	gyldigTilOgMed: Date
	motpartsPersonident: string
	motpartsRolle: string
	omraader: []
}

export const Visning = ({ data }: Data) => {
	return (
		<>
			<div className="person-visning_content">
				<ErrorBoundary>
					<TitleValue title="Områder" value={Formatters.arrayToString(data.omraader)} />
					<TitleValue title="Motparts personident" value={data.motpartsPersonident} />
					<TitleValue title="Motparts rolle" value={data.motpartsRolle} />
					<TitleValue
						title="Gyldig fra og med"
						value={Formatters.formatDate(data.gyldigFraOgMed)}
					/>
					<TitleValue
						title="Gyldig til og med"
						value={Formatters.formatDate(data.gyldigTilOgMed)}
					/>
				</ErrorBoundary>
			</div>
		</>
	)
}

export const PdlFullmakt = ({ data }: Data) => {
	if (!data) return null
	return (
		<div>
			<SubOverskrift label="Fullmakt" iconKind="fullmakt" />
			{/* @ts-ignore */}
			<Visning data={data} />
		</div>
	)
}
