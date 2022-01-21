import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FullmaktKodeverk } from '~/config/kodeverk'
import styled from 'styled-components'
import { FullmaktData, Relasjon } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { RelatertPerson } from '~/components/fagsystem/pdlf/visning/partials/RelatertPerson'

type Data = {
	data: FullmaktData
	relasjoner: Array<Relasjon>
}

type DataListe = {
	data: Array<FullmaktData>
	relasjoner: Array<Relasjon>
}

const Tema = styled.div`
	margin-bottom: 20px;
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

	return (
		<>
			<ErrorBoundary>
				<div className="person-visning_content">
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
				{fullmektig && <RelatertPerson data={fullmektig.relatertPerson} tittel="Fullmektig" />}
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
