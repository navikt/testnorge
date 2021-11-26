import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'

type Data = {
	data: FullmaktData
}

type DataListe = {
	data: Array<FullmaktData>
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
					<TitleValue
						title="Gyldig fra og med"
						value={Formatters.formatDate(data.gyldigFraOgMed)}
					/>
					<TitleValue
						title="Gyldig til og med"
						value={Formatters.formatDate(data.gyldigTilOgMed)}
					/>
					<TitleValue title="Områder" value={Formatters.omraaderArrayToString(data.omraader)} />
					<TitleValue title="Motparts personident" value={data.motpartsPersonident} />
					<TitleValue title="Motparts rolle" value={data.motpartsRolle} />
				</ErrorBoundary>
			</div>
		</>
	)
}

export const PdlFullmakt = ({ data }: DataListe) => {
	if (!data) return null
	return (
		<div>
			<SubOverskrift label="Fullmakt" iconKind="fullmakt" />
			{/* @ts-ignore */}
			<DollyFieldArray data={data} nested>
				{(fullmakt: FullmaktData) => <Visning data={fullmakt} />}
			</DollyFieldArray>
		</div>
	)
}
