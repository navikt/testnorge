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
	relasjoner: any //TODO: fix
}

type DataListe = {
	data: Array<FullmaktData>
	relasjoner: any // TODO: fix
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

export const Visning = ({ data, relasjoner }: Data) => {
	const fullmektigIdent = data.motpartsPersonident
	const fullmektig = relasjoner.find(
		(relasjon) => relasjon.relatertPerson?.ident === fullmektigIdent
	)
	const { fornavn, mellomnavn, etternavn } = fullmektig?.relatertPerson?.navn?.[0]

	return (
		<>
			<ErrorBoundary>
				<div className="person-visning_content">
					{/*<TitleValue title="Kilde" value={data.kilde} />*/}
					<TitleValue
						title="Gyldig fra og med"
						value={Formatters.formatDate(data.gyldigFraOgMed)}
					/>
					<TitleValue
						title="Gyldig til og med"
						value={Formatters.formatDate(data.gyldigTilOgMed)}
					/>
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
					<TitleValue title="Ident" value={fullmektig?.relatertPerson?.ident} />
					{/*<TitleValue title={identtype} value={relasjon?.relatertPerson?.ident} />*/}
					<TitleValue title="Fornavn" value={fornavn} />
					<TitleValue title="Mellomnavn" value={mellomnavn} />
					<TitleValue title="Etternavn" value={etternavn} />
					<TitleValue title="Kjønn" value={fullmektig?.relatertPerson?.kjoenn?.[0].kjoenn} />
				</div>
			</ErrorBoundary>
		</>
	)
}

export const Fullmakt = ({ data, relasjoner }: DataListe) => {
	if (!data || data.length < 1) return null
	const fullmaktRelasjoner = relasjoner?.filter(
		(relasjon) => relasjon.relasjonType === 'FULLMEKTIG'
	)

	return (
		<div>
			<SubOverskrift label="Fullmakt" iconKind="fullmakt" />

			<DollyFieldArray data={data} nested>
				{(fullmakt: FullmaktData) => (
					<Visning key={fullmakt.id} data={fullmakt} relasjoner={fullmaktRelasjoner} />
				)}
			</DollyFieldArray>
		</div>
	)
}
