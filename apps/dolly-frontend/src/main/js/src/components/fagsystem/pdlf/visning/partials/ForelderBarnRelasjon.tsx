import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { showLabel } from '@/utils/DataFormatter'
import { RelatertPerson } from '@/components/fagsystem/pdlf/visning/partials/RelatertPerson'
import { ForeldreBarnRelasjon, Relasjon } from '@/components/fagsystem/pdlf/PdlTypes'
import { RelatertPersonUtenId } from '@/components/fagsystem/pdlf/visning/partials/RelatertPersonUtenId'
import * as _ from 'lodash-es'
import { initialBarn, initialPdlPerson } from '@/components/fagsystem/pdlf/form/initialValues'
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
				{relasjon && (
					<RelatertPerson
						data={relasjon.relatertPerson}
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
	//TODO initialBarn må nok tilpasses
	const initForelderBarn = Object.assign(_.cloneDeep(initialBarn), data[idx])
	let initialValues = { forelderBarnRelasjon: initForelderBarn }

	const redigertForelderBarnPdlf = _.get(tmpPersoner, `${ident}.person.forelderBarnRelasjon`)?.find(
		(a: ForeldreBarnRelasjon) => a.id === forelderBarnRelasjonData.id
	)
	const redigertRelatertePersoner = _.get(tmpPersoner, `${ident}.relasjoner`)

	const slettetForelderBarnPdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertForelderBarnPdlf
	if (slettetForelderBarnPdlf) {
		return <pre style={{ margin: '0' }}>Opplysning slettet</pre>
	}

	const forelderBarnValues = redigertForelderBarnPdlf
		? redigertForelderBarnPdlf
		: forelderBarnRelasjonData
	let redigertForelderBarnValues = redigertForelderBarnPdlf
		? {
				//TODO initialBarn må nok tilpasses
				forelderBarnRelasjon: Object.assign(_.cloneDeep(initialBarn), redigertForelderBarnPdlf),
		  }
		: null

	const eksisterendeNyPerson = redigertRelatertePersoner
		? getEksisterendeNyPerson(redigertRelatertePersoner, forelderBarnValues?.relatertPerson, 'BARN')
		: getEksisterendeNyPerson(relasjoner, forelderBarnValues?.relatertPerson, 'BARN')

	if (eksisterendeNyPerson && initialValues?.forelderBarnRelasjon?.nyRelatertPerson) {
		initialValues.forelderBarnRelasjon.nyRelatertPerson = initialPdlPerson
	}

	if (eksisterendeNyPerson && redigertForelderBarnValues?.forelderBarnRelasjon?.nyRelatertPerson) {
		redigertForelderBarnValues.forelderBarnRelasjon.nyRelatertPerson = initialPdlPerson
	}

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
