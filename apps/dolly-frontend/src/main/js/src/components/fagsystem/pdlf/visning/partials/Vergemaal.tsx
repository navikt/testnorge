import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import Formatters from '@/utils/DataFormatter'
import { RelatertPerson } from '@/components/fagsystem/pdlf/visning/partials/RelatertPerson'
import { PersonData, Relasjon, VergemaalValues } from '@/components/fagsystem/pdlf/PdlTypes'
import { VergemaalKodeverk } from '@/config/kodeverk'
import * as _ from 'lodash-es'
import { initialPdlPerson, initialVergemaal } from '@/components/fagsystem/pdlf/form/initialValues'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'
import { getEksisterendeNyPerson } from '@/components/fagsystem/utils'

type Vergemaal = {
	vergemaalEmbete?: string
	embete?: string
	mandatType?: string
	vergeEllerFullmektig?: {
		omfang?: string
		motpartsPersonident?: string
	}
	sakType?: string
	type?: string
	gyldigFraOgMed: string
	gyldigTilOgMed: string
	vergeIdent?: string
	id: number
}

type VergemaalTypes = {
	data: Array<Vergemaal>
	tmpPersoner?: Array<PersonData>
	ident?: string
	erPdlVisning?: boolean
	relasjoner: Array<Relasjon>
}

type VergemaalLesTypes = {
	vergemaalData: Vergemaal
	relasjoner: Array<Relasjon>
	redigertRelatertePersoner?: Array<Relasjon>
	idx: number
}

type VergemaalVisningTypes = {
	vergemaalData: Vergemaal
	relasjoner: Array<Relasjon>
	idx: number
	data: Array<Vergemaal>
	tmpPersoner: Array<PersonData>
	ident: string
	erPdlVisning?: boolean
}

const VergemaalLes = ({
	vergemaalData,
	relasjoner,
	redigertRelatertePersoner = null,
	idx,
}: VergemaalLesTypes) => {
	if (!vergemaalData) {
		return null
	}

	const retatertPersonIdent = vergemaalData.vergeIdent
	const relasjon = relasjoner?.find((item) => item.relatertPerson?.ident === retatertPersonIdent)
	const relasjonRedigert = redigertRelatertePersoner?.find(
		(item) => item.relatertPerson?.ident === retatertPersonIdent
	)

	const harFullmektig = vergemaalData.sakType === 'FRE'

	return (
		<>
			<div className="person-visning_redigerbar" key={idx}>
				<TitleValue
					title="Fylkesmannsembete"
					kodeverk={VergemaalKodeverk.Fylkesmannsembeter}
					value={vergemaalData.vergemaalEmbete || vergemaalData.embete}
				/>
				<TitleValue
					title="Sakstype"
					kodeverk={VergemaalKodeverk.Sakstype}
					value={vergemaalData.sakType || vergemaalData.type}
				/>
				<TitleValue
					title="Mandattype"
					kodeverk={VergemaalKodeverk.Mandattype}
					value={vergemaalData.mandatType || vergemaalData.vergeEllerFullmektig?.omfang}
				/>
				<TitleValue
					title="Gyldig f.o.m."
					value={Formatters.formatDate(vergemaalData.gyldigFraOgMed)}
				/>
				<TitleValue
					title="Gyldig t.o.m."
					value={Formatters.formatDate(vergemaalData.gyldigTilOgMed)}
				/>
				{!relasjon && !relasjonRedigert && (
					<TitleValue
						title={harFullmektig ? 'Fullmektig' : 'Verge'}
						value={
							vergemaalData.vergeIdent || vergemaalData.vergeEllerFullmektig?.motpartsPersonident
						}
					/>
				)}
			</div>
			{(relasjonRedigert || relasjon) && (
				<RelatertPerson
					data={relasjonRedigert?.relatertPerson || relasjon?.relatertPerson}
					tittel={harFullmektig ? 'Fullmektig' : 'Verge'}
				/>
			)}
		</>
	)
}

const VergemaalVisning = ({
	vergemaalData,
	idx,
	data,
	tmpPersoner,
	ident,
	relasjoner,
	erPdlVisning,
}: VergemaalVisningTypes) => {
	const initVergemaal = Object.assign(_.cloneDeep(initialVergemaal), data[idx])
	let initialValues = { vergemaal: initVergemaal }
	initialValues.vergemaal.nyVergeIdent = initialPdlPerson

	const redigertVergemaalPdlf = _.get(tmpPersoner, `${ident}.person.vergemaal`)?.find(
		(a: VergemaalValues) => a.id === vergemaalData.id
	)
	const redigertRelatertePersoner = _.get(tmpPersoner, `${ident}.relasjoner`)

	const slettetVergemaalPdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertVergemaalPdlf
	if (slettetVergemaalPdlf) {
		return <pre style={{ margin: '0' }}>Opplysning slettet</pre>
	}

	const vergemaalValues = redigertVergemaalPdlf ? redigertVergemaalPdlf : vergemaalData
	let redigertVergemaalValues = redigertVergemaalPdlf
		? {
				vergemaal: Object.assign(_.cloneDeep(initialVergemaal), redigertVergemaalPdlf),
		  }
		: null
	if (redigertVergemaalValues) {
		redigertVergemaalValues.vergemaal.nyVergeIdent = initialPdlPerson
	}

	const eksisterendeNyPerson = redigertRelatertePersoner
		? getEksisterendeNyPerson(redigertRelatertePersoner, vergemaalValues?.vergeIdent, 'VERGE')
		: getEksisterendeNyPerson(relasjoner, vergemaalValues?.vergeIdent, 'VERGE')

	return erPdlVisning ? (
		<VergemaalLes vergemaalData={vergemaalData} relasjoner={relasjoner} idx={idx} />
	) : (
		<VisningRedigerbarConnector
			dataVisning={
				<VergemaalLes
					vergemaalData={vergemaalValues}
					redigertRelatertePersoner={redigertRelatertePersoner}
					relasjoner={relasjoner}
					idx={idx}
				/>
			}
			initialValues={initialValues}
			eksisterendeNyPerson={eksisterendeNyPerson}
			redigertAttributt={redigertVergemaalValues}
			path="vergemaal"
			ident={ident}
		/>
	)
}

export const Vergemaal = ({
	data,
	tmpPersoner,
	ident,
	erPdlVisning = false,
	relasjoner,
}: VergemaalTypes) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<div>
			<SubOverskrift label="Vergemål" iconKind="vergemaal" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} nested>
						{(vergemaal: VergemaalValues, idx: number) => (
							<VergemaalVisning
								vergemaalData={vergemaal}
								idx={idx}
								data={data}
								tmpPersoner={tmpPersoner}
								ident={ident}
								erPdlVisning={erPdlVisning}
								relasjoner={relasjoner}
							/>
						)}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</div>
	)
}
