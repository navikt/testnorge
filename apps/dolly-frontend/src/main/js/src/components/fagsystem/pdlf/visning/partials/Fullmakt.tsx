import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import Formatters from '@/utils/DataFormatter'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FullmaktKodeverk } from '@/config/kodeverk'
import styled from 'styled-components'
import { FullmaktData, Relasjon } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { RelatertPerson } from '@/components/fagsystem/pdlf/visning/partials/RelatertPerson'
import { FullmaktValues, PersonData } from '@/components/fagsystem/pdlf/PdlTypes'
import * as _ from 'lodash-es'
import { initialFullmakt, initialPdlPerson } from '@/components/fagsystem/pdlf/form/initialValues'
import { getEksisterendeNyPerson } from '@/components/fagsystem/utils'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'

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

const FullmaktLes = ({ fullmaktData, relasjoner, redigertRelatertePersoner = null, idx }) => {
	if (!fullmaktData) {
		return null
	}

	const fullmektigIdent = fullmaktData.motpartsPersonident
	const fullmektig = relasjoner?.find(
		(relasjon) => relasjon.relatertPerson?.ident === fullmektigIdent
	)

	// TODO: Lag ny visning av tema/områder

	return (
		<>
			<div className="person-visning_redigerbar" key={idx}>
				<div className="person-visning_content">
					<TitleValue
						title="Gyldig fra og med"
						value={Formatters.formatDate(fullmaktData.gyldigFraOgMed)}
					/>
					<TitleValue
						title="Gyldig til og med"
						value={Formatters.formatDate(fullmaktData.gyldigTilOgMed)}
					/>
					{!fullmektig && (
						<TitleValue
							title="Fullmektig"
							value={
								fullmaktData.motpartsPersonident ||
								fullmaktData.vergeEllerFullmektig?.motpartsPersonident
							}
						/>
					)}
				</div>
				<Tema>
					<h4>Tema</h4>
					{fullmaktData.omraader.map((omraade: string) =>
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
			</div>
			{(fullmektig || redigertRelatertePersoner) && (
				<RelatertPerson
					data={fullmektig.relatertPerson || redigertRelatertePersoner}
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
	console.log('fullmaktData: ', fullmaktData) //TODO - SLETT MEG
	//TODO: Trenger vi denne?
	initialValues.fullmakt.nyFullmektig = initialPdlPerson

	const redigertFullmaktPdlf = _.get(tmpPersoner, `${ident}.person.fullmakt`)?.find(
		(a: FullmaktValues) => a.id === fullmaktData.id
	)
	const redigertRelatertePersoner = _.get(tmpPersoner, `${ident}.relasjoner`)

	const slettetFullmaktPdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertFullmaktPdlf
	if (slettetFullmaktPdlf) {
		return <pre style={{ margin: '0' }}>Opplysning slettet</pre>
	}

	const fullmaktValues = redigertFullmaktPdlf ? redigertFullmaktPdlf : fullmaktData
	let redigertFullmaktValues = redigertFullmaktPdlf
		? {
				fullmakt: Object.assign(_.cloneDeep(initialFullmakt), redigertFullmaktPdlf),
		  }
		: null

	//TODO: Trenger vi denne?
	if (redigertFullmaktValues) {
		redigertFullmaktValues.fullmakt.nyFullmektig = initialPdlPerson
	}

	const eksisterendeNyPerson = redigertRelatertePersoner
		? getEksisterendeNyPerson(
				redigertRelatertePersoner,
				fullmaktValues?.motpartsPersonident,
				'FULLMEKTIG'
		  )
		: getEksisterendeNyPerson(relasjoner, fullmaktValues?.motpartsPersonident, 'FULLMEKTIG')

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

export const Fullmakt = ({ data, tmpPersoner, ident, relasjoner }: DataListe) => {
	if (!data || data.length < 1) {
		return null
	}
	const fullmaktRelasjoner = relasjoner?.filter(
		(relasjon) => relasjon.relasjonType === 'FULLMEKTIG'
	)

	return (
		<div>
			<SubOverskrift label="Fullmakt" iconKind="fullmakt" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} nested>
						{(fullmakt: FullmaktData, idx: number) => (
							<FullmaktVisning
								fullmaktData={fullmakt}
								// key={fullmakt.id}
								idx={idx}
								data={data}
								tmpPersoner={tmpPersoner}
								ident={ident}
								relasjoner={fullmaktRelasjoner}
							/>
						)}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</div>
	)
}
