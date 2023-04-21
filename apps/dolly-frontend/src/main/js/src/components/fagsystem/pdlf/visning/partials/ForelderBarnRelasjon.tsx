import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { showLabel } from '@/utils/DataFormatter'
import { RelatertPerson } from '@/components/fagsystem/pdlf/visning/partials/RelatertPerson'
import { ForeldreBarnRelasjon, Relasjon } from '@/components/fagsystem/pdlf/PdlTypes'
import { RelatertPersonUtenId } from '@/components/fagsystem/pdlf/visning/partials/RelatertPersonUtenId'
import * as _ from 'lodash-es'
import {
	initialBarn,
	initialForelder,
	initialPdlPerson,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { getEksisterendeNyPerson } from '@/components/fagsystem/utils'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'

type FamilieRelasjonerData = {
	data: Array<ForeldreBarnRelasjon>
	relasjoner: Array<Relasjon>
}

type VisningData = {
	data: ForeldreBarnRelasjon
	relasjoner: Array<Relasjon>
}

const ForelderBarnRelasjonLes = ({
	forelderBarnData,
	redigertRelatertePersoner = null,
	relasjoner,
	idx,
}: VisningData) => {
	const relatertPersonIdent =
		forelderBarnData.relatertPerson || forelderBarnData.relatertPersonsIdent
	const relasjon = relasjoner?.find((item) => item.relatertPerson?.ident === relatertPersonIdent)
	const relasjonRedigert = redigertRelatertePersoner?.find(
		(item) => item.relatertPerson?.ident === relatertPersonIdent
	)
	const relatertPersonUtenId = forelderBarnData.relatertPersonUtenFolkeregisteridentifikator

	return (
		<>
			<ErrorBoundary>
				<div className="person-visning_content">
					{!relasjoner && <TitleValue title="Relatert person" value={relatertPersonIdent} />}
					{forelderBarnData.relatertPersonsRolle === 'BARN' && (
						<TitleValue title="Rolle for barn" value={forelderBarnData.minRolleForPerson} />
					)}
				</div>
				{(relasjon || relasjonRedigert) && (
					<RelatertPerson
						data={relasjonRedigert?.relatertPerson || relasjon?.relatertPerson}
						tittel={showLabel('pdlRelasjonTyper', forelderBarnData.relatertPersonsRolle)}
					/>
				)}
				{relatertPersonUtenId && (
					<RelatertPersonUtenId
						data={relatertPersonUtenId}
						tittel={showLabel('pdlRelasjonTyper', forelderBarnData.relatertPersonsRolle)}
					/>
				)}
			</ErrorBoundary>
		</>
	)
}

export const ForelderBarnRelasjonVisning = ({
	forelderBarnRelasjonData,
	idx,
	data,
	tmpPersoner,
	ident,
	relasjoner,
}: FamilieRelasjonerData) => {
	// console.log('forelderBarnRelasjonData: ', forelderBarnRelasjonData) //TODO - SLETT MEG
	console.log('data: ', data) //TODO - SLETT MEG
	console.log('relasjoner: ', relasjoner) //TODO - SLETT MEG
	// console.log('data[idx]: ', data[idx]) //TODO - SLETT MEG
	const initForelderBarn = Object.assign(
		_.cloneDeep(data[idx].relatertPersonsRolle === 'BARN' ? initialBarn : initialForelder),
		data[idx]
	)
	let initialValues = { forelderBarnRelasjon: initForelderBarn }

	const redigertForelderBarnPdlf = _.get(tmpPersoner, `${ident}.person.forelderBarnRelasjon`)?.find(
		(a: ForeldreBarnRelasjon) => a.id === forelderBarnRelasjonData.id
	)
	const redigertRelatertePersoner = _.get(tmpPersoner, `${ident}.relasjoner`)

	const slettetForelderBarnPdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertForelderBarnPdlf
	if (slettetForelderBarnPdlf) {
		return <pre style={{ margin: '0' }}>Opplysning slettet</pre>
	}
	// console.log('redigertForelderBarnPdlf: ', redigertForelderBarnPdlf) //TODO - SLETT MEG
	const forelderBarnValues = redigertForelderBarnPdlf
		? redigertForelderBarnPdlf
		: forelderBarnRelasjonData
	let redigertForelderBarnValues = redigertForelderBarnPdlf
		? {
				forelderBarnRelasjon: Object.assign(
					_.cloneDeep(
						redigertForelderBarnPdlf.relatertPersonsRolle === 'BARN' ? initialBarn : initialForelder
					),
					redigertForelderBarnPdlf
				),
		  }
		: null

	// console.log('forelderBarnValues: ', forelderBarnValues) //TODO - SLETT MEG
	// console.log('relasjoner: ', relasjoner) //TODO - SLETT MEG
	const eksisterendeNyPerson = redigertRelatertePersoner
		? getEksisterendeNyPerson(redigertRelatertePersoner, forelderBarnValues?.relatertPerson, [
				'FAMILIERELASJON_BARN',
				'FAMILIERELASJON_FORELDER',
		  ])
		: getEksisterendeNyPerson(relasjoner, forelderBarnValues?.relatertPerson, [
				'FAMILIERELASJON_BARN',
				'FAMILIERELASJON_FORELDER',
		  ])

	if (eksisterendeNyPerson && initialValues?.forelderBarnRelasjon?.nyRelatertPerson) {
		initialValues.forelderBarnRelasjon.nyRelatertPerson = initialPdlPerson
	}

	if (eksisterendeNyPerson && redigertForelderBarnValues?.forelderBarnRelasjon?.nyRelatertPerson) {
		redigertForelderBarnValues.forelderBarnRelasjon.nyRelatertPerson = initialPdlPerson
	}

	console.log('eksisterendeNyPerson: ', eksisterendeNyPerson) //TODO - SLETT MEG
	const getForeldreansvar = () => {
		const relasjon = relasjoner?.find(
			(relasjon) =>
				relasjon?.relatertPerson?.ident === forelderBarnValues?.relatertPerson &&
				relasjon?.relasjonType === 'FAMILIERELASJON_BARN'
		)
		return relasjon?.relatertPerson?.foreldreansvar
	}
	const foreldreansvar = getForeldreansvar()
	// console.log('foreldreansvar(): ', foreldreansvar()) //TODO - SLETT MEG
	if (foreldreansvar) {
		initialValues.foreldreansvar = foreldreansvar[0]
		//TODO: Ta høyde for flere foreldreansvar
	}
	console.log('initialValues: ', initialValues) //TODO - SLETT MEG

	return (
		<VisningRedigerbarConnector
			dataVisning={
				<ForelderBarnRelasjonLes
					forelderBarnData={forelderBarnValues}
					redigertRelatertePersoner={redigertRelatertePersoner}
					relasjoner={relasjoner}
					idx={idx}
				/>
			}
			initialValues={initialValues}
			eksisterendeNyPerson={eksisterendeNyPerson}
			redigertAttributt={redigertForelderBarnValues}
			path="forelderBarnRelasjon"
			ident={ident}
		/>
	)
}

export const ForelderBarnRelasjon = ({
	data,
	tmpPersoner,
	ident,
	relasjoner,
}: FamilieRelasjonerData) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<div>
			<SubOverskrift label="Barn/foreldre" iconKind="relasjoner" />
			<DollyFieldArray data={data} nested>
				{(forelderBarnRelasjon: ForeldreBarnRelasjon, idx: number) => (
					<ForelderBarnRelasjonVisning
						forelderBarnRelasjonData={forelderBarnRelasjon}
						idx={idx}
						data={data}
						tmpPersoner={tmpPersoner}
						ident={ident}
						relasjoner={relasjoner}
					/>
				)}
			</DollyFieldArray>
		</div>
	)
}
