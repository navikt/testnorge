import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, showKodeverkLabel } from '@/utils/DataFormatter'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FullmaktKodeverk } from '@/config/kodeverk'
import styled from 'styled-components'
import { FullmaktData, Relasjon } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { RelatertPerson } from '@/components/fagsystem/pdlf/visning/partials/RelatertPerson'
import { FullmaktValues, PersonData } from '@/components/fagsystem/pdlf/PdlTypes'
import _ from 'lodash'
import { initialFullmakt, initialPdlPerson } from '@/components/fagsystem/pdlf/form/initialValues'
import { getEksisterendeNyPerson } from '@/components/fagsystem/utils'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'
import { OpplysningSlettet } from '@/components/fagsystem/pdlf/visning/visningRedigerbar/OpplysningSlettet'
import React from 'react'

type Data = {
	fullmaktData: FullmaktData
	idx: number
	data: Array<FullmaktData>
	tmpPersoner?: Array<PersonData>
	ident?: string
	relasjoner: Array<Relasjon>
}

type DataListe = {
	data: Array<FullmaktData>
	tmpPersoner?: Array<PersonData>
	ident?: string
	relasjoner: Array<Relasjon>
	erRedigerbar?: boolean
}

const Omraader = styled(TitleValue)`
	&& {
		margin-bottom: 20px;
	}
`

const FullmaktLes = ({ fullmaktData, relasjoner, redigertRelatertePersoner = null, idx }) => {
	if (!fullmaktData) {
		return null
	}

	const fullmektigIdent = fullmaktData.motpartsPersonident
	const fullmektig = relasjoner?.find(
		(relasjon) => relasjon.relatertPerson?.ident === fullmektigIdent,
	)
	const fullmektigRedigert = redigertRelatertePersoner?.find(
		(item) => item.relatertPerson?.ident === fullmektigIdent,
	)

	const omraader = fullmaktData.omraader
		?.map((omraade) => showKodeverkLabel(FullmaktKodeverk.Tema, omraade))
		?.join(', ')

	return (
		<>
			<div className="person-visning_redigerbar" key={idx}>
				<Omraader title="Områder" value={omraader} size={'full-width'} />
				<div className="person-visning_content">
					<TitleValue title="Gyldig fra og med" value={formatDate(fullmaktData.gyldigFraOgMed)} />
					<TitleValue title="Gyldig til og med" value={formatDate(fullmaktData.gyldigTilOgMed)} />
					{!fullmektig && !fullmektigRedigert && (
						<TitleValue
							title="Fullmektig"
							value={
								fullmaktData.motpartsPersonident ||
								fullmaktData.vergeEllerFullmektig?.motpartsPersonident
							}
						/>
					)}
				</div>
			</div>
			{(fullmektig || fullmektigRedigert) && (
				<RelatertPerson
					data={fullmektigRedigert?.relatertPerson || fullmektig?.relatertPerson}
					tittel="Fullmektig"
				/>
			)}
		</>
	)
}

export const FullmaktVisning = ({
	fullmaktData,
	idx,
	data,
	tmpPersoner,
	ident,
	relasjoner,
}: Data) => {
	const initFullmakt = Object.assign(_.cloneDeep(initialFullmakt), data[idx])
	let initialValues = { fullmakt: initFullmakt }

	const redigertFullmaktPdlf = _.get(tmpPersoner, `${ident}.person.fullmakt`)?.find(
		(a: FullmaktValues) => a.id === fullmaktData.id,
	)
	const redigertRelatertePersoner = _.get(tmpPersoner, `${ident}.relasjoner`)

	const slettetFullmaktPdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertFullmaktPdlf
	if (slettetFullmaktPdlf) {
		return <OpplysningSlettet />
	}

	const fullmaktValues = redigertFullmaktPdlf ? redigertFullmaktPdlf : fullmaktData
	let redigertFullmaktValues = redigertFullmaktPdlf
		? {
				fullmakt: Object.assign(_.cloneDeep(initialFullmakt), redigertFullmaktPdlf),
			}
		: null

	const eksisterendeNyPerson = redigertRelatertePersoner
		? getEksisterendeNyPerson(redigertRelatertePersoner, fullmaktValues?.motpartsPersonident, [
				'FULLMEKTIG',
			])
		: getEksisterendeNyPerson(relasjoner, fullmaktValues?.motpartsPersonident, ['FULLMEKTIG'])

	if (eksisterendeNyPerson && initialValues?.fullmakt?.nyFullmektig) {
		initialValues.fullmakt.nyFullmektig = initialPdlPerson
	}

	if (eksisterendeNyPerson && redigertFullmaktValues?.fullmakt?.nyFullmektig) {
		redigertFullmaktValues.fullmakt.nyFullmektig = initialPdlPerson
	}

	return (
		<VisningRedigerbarConnector
			dataVisning={
				<FullmaktLes
					fullmaktData={fullmaktValues}
					redigertRelatertePersoner={redigertRelatertePersoner}
					relasjoner={relasjoner}
					idx={idx}
				/>
			}
			initialValues={initialValues}
			eksisterendeNyPerson={eksisterendeNyPerson}
			redigertAttributt={redigertFullmaktValues}
			path="fullmakt"
			ident={ident}
		/>
	)
}

export const Fullmakt = ({
	data,
	tmpPersoner,
	ident,
	relasjoner,
	erRedigerbar = true,
}: DataListe) => {
	if (!data || data.length < 1) {
		return null
	}
	const fullmaktRelasjoner = relasjoner?.filter(
		(relasjon) => relasjon.relasjonType === 'FULLMEKTIG',
	)

	return (
		<div>
			<SubOverskrift label="Fullmakt" iconKind="fullmakt" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} nested>
						{(fullmakt: FullmaktData, idx: number) =>
							erRedigerbar ? (
								<FullmaktVisning
									fullmaktData={fullmakt}
									idx={idx}
									data={data}
									tmpPersoner={tmpPersoner}
									ident={ident}
									relasjoner={fullmaktRelasjoner}
								/>
							) : (
								<FullmaktLes fullmaktData={fullmakt} relasjoner={relasjoner} idx={idx} />
							)
						}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</div>
	)
}
