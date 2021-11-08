import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FullmaktKodeverk } from '~/config/kodeverk'
import styled from 'styled-components'

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
	id: number
}

type Fullmektig = {
	fornavn: string
	mellomnavn?: string
	etternavn: string
	ident: string
	identtype: string
	kjonn: string
}

const Tema = styled.div`
	h4 {
		width: 100%;
		margin-bottom: 10px;
		margin-top: 0px;
	}
	TitleValue {
		margin-bottom: 5px;
	}
`

export const Visning = ({ data }: Data) => {
	const { etternavn, fornavn, ident, identtype, kjonn, mellomnavn } = data.fullmektig

	return (
		<>
			<ErrorBoundary>
				<div className="person-visning_content">
					<TitleValue title="Kilde" value={data.kilde} />
					<TitleValue title="Gyldig fra og med" value={Formatters.formatDate(data.gyldigFom)} />
					<TitleValue title="Gyldig til og med" value={Formatters.formatDate(data.gyldigTom)} />
				</div>
				<Tema>
					<h4>Tema</h4>
					{data.omraader.map((omraade: string) =>
						omraade.includes('*') ? (
							<TitleValue key={omraade} value={'Alle (*)'} size={'full-width'} />
						) : (
							<TitleValue
								key={omraade}
								kodeverk={FullmaktKodeverk.Tema}
								value={omraade}
								size={'full-width'}
							/>
						)
					)}
				</Tema>
				<div className="person-visning_content">
					<h4 style={{ width: '100%' }}>Fullmektig</h4>
					<TitleValue title={identtype} value={ident} />
					<TitleValue title="Fornavn" value={fornavn} />
					<TitleValue title="Mellomnavn" value={mellomnavn} />
					<TitleValue title="Etternavn" value={etternavn} />
					<TitleValue title="Kjønn" value={Formatters.kjonnToString(kjonn)} />
				</div>
			</ErrorBoundary>
		</>
	)
}

export const Fullmakt = ({ data }: DataListe) => {
	if (!data || data.length < 1) return null
	return (
		<div>
			<SubOverskrift label="Fullmakt" iconKind="fullmakt" />

			<DollyFieldArray data={data} nested>
				{(fullmakt: FullmaktData) => <Visning key={fullmakt.id} data={fullmakt} />}
			</DollyFieldArray>
		</div>
	)
}
