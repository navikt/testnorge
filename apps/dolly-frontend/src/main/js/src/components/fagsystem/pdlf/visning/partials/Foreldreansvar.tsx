import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { initialForeldreansvar } from '@/components/fagsystem/pdlf/form/initialValues'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import * as React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import * as _ from 'lodash-es'
import { formatDate } from '@/utils/DataFormatter'
import { AdresseKodeverk } from '@/config/kodeverk'
import { getEksisterendeNyPerson } from '@/components/fagsystem/utils'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'
import styled from 'styled-components'
import { PdlDataVisning } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataVisning'

const StyledPdlData = styled.div`
	margin-bottom: 10px;
	display: flex;
	flex-wrap: wrap;

	p {
		margin: 0;
	}
`

const ForeldreansvarLes = ({ foreldreansvarData, redigertRelatertePersoner, relasjoner, idx }) => {
	if (!foreldreansvarData) {
		return null
	}

	const ansvarlig = relasjoner?.find(
		(relasjon) =>
			relasjon.relasjonType === 'FORELDREANSVAR_FORELDER' &&
			relasjon.relatertPerson?.ident === foreldreansvarData.ansvarlig
	)
	// TODO: Er det forskjellige for mor/far?
	//TODO: Ta med redigerteRelatertePersoner

	return (
		<>
			<div className="person-visning_content" key={idx}>
				<TitleValue title="Hvem har ansvaret" value={_.capitalize(foreldreansvarData.ansvar)} />
				{!ansvarlig && !foreldreansvarData.ansvarligUtenIdentifikator && (
					<TitleValue title="Ident" value={foreldreansvarData.ansvarlig} />
				)}
				{ansvarlig && (
					<div className="flexbox--full-width">
						<h4 style={{ marginTop: '5px' }}>Ansvarlig</h4>
						<div className="person-visning_content" key={idx} style={{ marginBottom: 0 }}>
							<TitleValue title="Ident" value={ansvarlig.relatertPerson?.ident} visKopier />
							<TitleValue title="Fornavn" value={ansvarlig.relatertPerson?.navn?.[0]?.fornavn} />
							<TitleValue
								title="Mellomnavn"
								value={ansvarlig.relatertPerson?.navn?.[0]?.mellomnavn}
							/>
							<TitleValue
								title="Etternavn"
								value={ansvarlig.relatertPerson?.navn?.[0]?.etternavn}
							/>
							<TitleValue title="Kjønn" value={ansvarlig.relatertPerson?.kjoenn?.[0]?.kjoenn} />
							<TitleValue
								title="Fødselsdato"
								value={formatDate(ansvarlig.relatertPerson?.foedsel?.[0]?.foedselsdato)}
							/>
							<TitleValue
								title="Statsborgerskap"
								value={ansvarlig.relatertPerson?.statsborgerskap?.[0]?.landkode}
								kodeverk={AdresseKodeverk.StatsborgerskapLand}
							/>
						</div>
					</div>
				)}
				{foreldreansvarData.ansvarligUtenIdentifikator && (
					<div className="flexbox--full-width">
						<h4 style={{ marginTop: '5px' }}>Ansvarlig uten identifikator</h4>
						<div className="person-visning_content" key={idx}>
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
			<StyledPdlData>
				<PdlDataVisning ident={foreldreansvarData.ansvarlig} />
				<p>
					<i>Hold pekeren over PDL for å se dataene som finnes på ansvarlig i PDL</i>
				</p>
			</StyledPdlData>
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
}) => {
	const initForeldreansvar = Object.assign(_.cloneDeep(initialForeldreansvar), data[idx])
	let initialValues = { foreldreansvar: initForeldreansvar }

	const redigertForeldreansvarPdlf = _.get(tmpPersoner, `${ident}.person.foreldreansvar`)?.find(
		(a) => a.id === foreldreansvarData.id
	)
	const redigertRelatertePersoner = _.get(tmpPersoner, `${ident}.relasjoner`)

	const slettetForeldreansvarPdlf =
		tmpPersoner?.hasOwnProperty(ident) && !redigertForeldreansvarPdlf
	if (slettetForeldreansvarPdlf) {
		return <pre style={{ margin: '0' }}>Opplysning slettet</pre>
	}

	const foreldreansvarValues = redigertForeldreansvarPdlf
		? redigertForeldreansvarPdlf
		: foreldreansvarData

	let redigertForeldreansvarValues = redigertForeldreansvarPdlf
		? {
				foreldreansvar: Object.assign(
					_.cloneDeep(initialForeldreansvar),
					redigertForeldreansvarPdlf
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

	// if (eksisterendeNyPerson && initialValues?.forelderBarnRelasjon?.nyRelatertPerson) {
	// 	initialValues.forelderBarnRelasjon.nyRelatertPerson = initialPdlPerson
	// }
	//
	// if (eksisterendeNyPerson && redigertForelderBarnValues?.forelderBarnRelasjon?.nyRelatertPerson) {
	// 	redigertForelderBarnValues.forelderBarnRelasjon.nyRelatertPerson = initialPdlPerson
	// }

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
		/>
	)
}

export const ForeldreansvarVisning = ({ data, tmpPersoner, ident, relasjoner }) => {
	if (!data || data?.length === 0) {
		return null
	}

	return (
		<div>
			<SubOverskrift label="Foreldreansvar" iconKind="foreldreansvar" />
			<DollyFieldArray data={data} nested>
				{(foreldreansvar, idx) => (
					<ForeldreansvarEnkeltvisning
						foreldreansvarData={foreldreansvar}
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
