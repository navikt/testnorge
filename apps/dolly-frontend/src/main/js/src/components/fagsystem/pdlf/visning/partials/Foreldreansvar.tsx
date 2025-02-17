import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { getInitialForeldreansvar } from '@/components/fagsystem/pdlf/form/initialValues'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import * as React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import * as _ from 'lodash-es'
import { formatDate } from '@/utils/DataFormatter'
import { AdresseKodeverk } from '@/config/kodeverk'
import { getEksisterendeNyPerson } from '@/components/fagsystem/utils'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'
import { OpplysningSlettet } from '@/components/fagsystem/pdlf/visning/visningRedigerbar/OpplysningSlettet'
import { RelatertPerson } from '@/components/fagsystem/pdlf/visning/partials/RelatertPerson'
import { useParams } from 'react-router'
import { useGruppeIdenter } from '@/utils/hooks/useGruppe'

const ForeldreansvarLes = ({ foreldreansvarData, redigertRelatertePersoner, relasjoner, idx }) => {
	if (!foreldreansvarData) {
		return null
	}

	const ansvarlig = relasjoner?.find(
		(relasjon) =>
			relasjon.relasjonType === 'FORELDREANSVAR_FORELDER' &&
			relasjon.relatertPerson?.ident === foreldreansvarData.ansvarlig,
	)

	const ansvarligRedigert = redigertRelatertePersoner?.find(
		(relasjon) =>
			relasjon.relasjonType === 'FORELDREANSVAR_FORELDER' &&
			relasjon.relatertPerson?.ident === foreldreansvarData.ansvarlig,
	)

	return (
		<>
			<div className="person-visning_content" key={idx}>
				<>
					<TitleValue title="Hvem har ansvaret" value={_.capitalize(foreldreansvarData.ansvar)} />
					<TitleValue title="Ansvarssubjekt (barnet)" value={foreldreansvarData.ansvarssubjekt} />
					<TitleValue
						title="Gyldig fra og med"
						value={formatDate(foreldreansvarData.gyldigFraOgMed)}
					/>
					<TitleValue
						title="Gyldig til og med"
						value={formatDate(foreldreansvarData.gyldigTilOgMed)}
					/>
				</>
				{!ansvarlig && !ansvarligRedigert && !foreldreansvarData.ansvarligUtenIdentifikator && (
					<TitleValue title="Ident" value={foreldreansvarData.ansvarlig} />
				)}
				{(ansvarlig || ansvarligRedigert) && (
					<RelatertPerson
						data={ansvarligRedigert?.relatertPerson || ansvarlig?.relatertPerson}
						tittel="Ansvarlig"
						marginTop="10px"
					/>
				)}
				{foreldreansvarData.ansvarligUtenIdentifikator && (
					<div className="flexbox--full-width">
						<h4 style={{ marginTop: '5px' }}>Ansvarlig uten identifikator</h4>
						<div className="person-visning_content" key={idx} style={{ marginBottom: 0 }}>
							<TitleValue
								title="Fødselsdato"
								value={formatDate(foreldreansvarData.ansvarligUtenIdentifikator.foedselsdato)}
							/>
							<TitleValue
								title="Kjønn"
								value={foreldreansvarData.ansvarligUtenIdentifikator.kjoenn}
							/>
							<TitleValue
								title="Fornavn"
								value={foreldreansvarData.ansvarligUtenIdentifikator.navn?.fornavn}
							/>
							<TitleValue
								title="Mellomnavn"
								value={foreldreansvarData.ansvarligUtenIdentifikator.navn?.mellomnavn}
							/>
							<TitleValue
								title="Etternavn"
								value={foreldreansvarData.ansvarligUtenIdentifikator.navn?.etternavn}
							/>
							<TitleValue
								title="Statsborgerskap"
								value={foreldreansvarData.ansvarligUtenIdentifikator.statsborgerskap}
								kodeverk={AdresseKodeverk.StatsborgerskapLand}
							/>
						</div>
					</div>
				)}
			</div>
		</>
	)
}

export const ForeldreansvarEnkeltvisning = ({
	foreldreansvarData,
	idx,
	data,
	tmpPersoner,
	ident,
	relasjoner,
	personValues,
}) => {
	const { gruppeId } = useParams()
	const { identer: gruppeIdenter } = useGruppeIdenter(gruppeId)

	const initForeldreansvar = Object.assign(_.cloneDeep(getInitialForeldreansvar), data[idx])
	let initialValues = { foreldreansvar: initForeldreansvar }

	const redigertForeldreansvarPdlf = _.get(tmpPersoner, `${ident}.person.foreldreansvar`)?.find(
		(a) => a.id === foreldreansvarData.id,
	)

	const redigertForelderBarnRelasjonPdlf = _.get(
		tmpPersoner,
		`${ident}.person.forelderBarnRelasjon`,
	)

	const redigertRelatertePersoner = _.get(tmpPersoner, `${ident}.relasjoner`)

	const slettetForeldreansvarPdlf =
		tmpPersoner?.hasOwnProperty(ident) && !redigertForeldreansvarPdlf

	if (slettetForeldreansvarPdlf) {
		return <OpplysningSlettet />
	}

	const foreldreansvarValues = redigertForeldreansvarPdlf
		? redigertForeldreansvarPdlf
		: foreldreansvarData

	let redigertForeldreansvarValues = redigertForeldreansvarPdlf
		? {
				foreldreansvar: Object.assign(
					_.cloneDeep(getInitialForeldreansvar),
					redigertForeldreansvarPdlf,
				),
			}
		: null

	const eksisterendeNyPerson = redigertRelatertePersoner
		? getEksisterendeNyPerson(redigertRelatertePersoner, foreldreansvarValues?.ansvarlig, [
				'FORELDREANSVAR_FORELDER',
			])
		: getEksisterendeNyPerson(relasjoner, foreldreansvarValues?.ansvarlig, [
				'FORELDREANSVAR_FORELDER',
			])

	let personValuesMedRedigert = _.cloneDeep(personValues)
	if (redigertForelderBarnRelasjonPdlf && personValuesMedRedigert) {
		personValuesMedRedigert.forelderBarnRelasjon = redigertForelderBarnRelasjonPdlf
	}

	const erIGruppe = gruppeIdenter?.some(
		(person) => person.ident === initialValues?.foreldreansvar?.ansvarlig,
	)
	const relatertPersonInfo = erIGruppe
		? {
				ident: initialValues?.foreldreansvar?.ansvarlig,
			}
		: null

	return (
		<VisningRedigerbarConnector
			dataVisning={
				<ForeldreansvarLes
					foreldreansvarData={foreldreansvarValues}
					redigertRelatertePersoner={redigertRelatertePersoner}
					relasjoner={relasjoner}
					idx={idx}
				/>
			}
			initialValues={initialValues}
			eksisterendeNyPerson={eksisterendeNyPerson}
			redigertAttributt={redigertForeldreansvarValues}
			path="foreldreansvar"
			ident={ident}
			personValues={personValuesMedRedigert}
			relatertPersonInfo={relatertPersonInfo}
		/>
	)
}

export const ForeldreansvarVisning = ({
	data,
	tmpPersoner,
	ident,
	relasjoner,
	personValues,
	erRedigerbar = true,
}) => {
	if (!data || data?.length === 0) {
		return null
	}

	const tmpForeldreansvar = tmpPersoner?.[ident]?.person?.foreldreansvar
	const foreldreansvarData = tmpForeldreansvar
		? data.length >= tmpForeldreansvar.length
			? data
			: tmpForeldreansvar
		: data

	return (
		<div>
			<SubOverskrift label="Foreldreansvar" iconKind="foreldreansvar" />
			<DollyFieldArray data={foreldreansvarData} nested>
				{(foreldreansvar, idx) =>
					erRedigerbar ? (
						<ForeldreansvarEnkeltvisning
							foreldreansvarData={foreldreansvar}
							idx={idx}
							data={foreldreansvarData}
							tmpPersoner={tmpPersoner}
							ident={ident}
							relasjoner={relasjoner}
							personValues={personValues}
						/>
					) : (
						<ForeldreansvarLes
							foreldreansvarData={foreldreansvar}
							relasjoner={relasjoner}
							idx={idx}
						/>
					)
				}
			</DollyFieldArray>
		</div>
	)
}
