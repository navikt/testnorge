import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { Historikk } from '~/components/ui/historikk/Historikk'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

type Data = {
	data: FullmaktData
}

type DataListe = {
	data: Array<FullmaktData>
}

type FullmaktData = {
	gyldigFom: Date
	gyldigTom: Date
	kilde: string
	omraader: []
	fullmektig: Fullmektig
}

type Fullmektig = {
	fornavn: string
	mellomnavn?: string
	etternavn: string
	ident: string
	identtype: string
	kjonn: string
}

export const Visning = ({ data }: Data) => {
	const fullmektig = data.fullmektig

	return (
		<>
			<div className="person-visning_content">
				<ErrorBoundary>
					<TitleValue title="Områder" value={Formatters.omraaderArrayToString(data.omraader)} />
					<TitleValue title="Kilde" value={data.kilde} />
					<TitleValue title="Gyldig fra og med" value={Formatters.formatDate(data.gyldigFom)} />
					<TitleValue title="Gyldig til og med" value={Formatters.formatDate(data.gyldigTom)} />
				</ErrorBoundary>
			</div>
			<h4 style={{ marginTop: '0px' }}>Fullmektig</h4>
			<div className="person-visning_content">
				<TitleValue title={fullmektig.identtype} value={fullmektig.ident} />
				<TitleValue title="Fornavn" value={fullmektig.fornavn} />
				<TitleValue title="Mellomnavn" value={fullmektig.mellomnavn} />
				<TitleValue title="Etternavn" value={fullmektig.etternavn} />
				<TitleValue title="Kjønn" value={Formatters.kjonnToString(fullmektig.kjonn)} />
			</div>
		</>
	)
}

export const Fullmakt = ({ data }: DataListe) => {
	if (!data || data.length < 1) return null
	return (
		<div>
			<SubOverskrift label="Fullmakt" iconKind="fullmakt" />
			{/* @ts-ignore */}
			<Historikk component={Visning} data={data} />
		</div>
	)
}
