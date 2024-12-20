import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import { RelatertPerson } from '@/components/fagsystem/pdlf/visning/partials/RelatertPerson'
import { Relasjon, SivilstandData } from '@/components/fagsystem/pdlf/PdlTypes'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'
import {
	getInitialSivilstand,
	initialPdlPerson,
} from '@/components/fagsystem/pdlf/form/initialValues'
import * as _ from 'lodash-es'
import React from 'react'
import { getEksisterendeNyPerson } from '@/components/fagsystem/utils'
import { OpplysningSlettet } from '@/components/fagsystem/pdlf/visning/visningRedigerbar/OpplysningSlettet'
import { useParams } from 'react-router'
import { useGruppeIdenter } from '@/utils/hooks/useGruppe'

type SivilstandTypes = {
	data: Array<SivilstandData>
	relasjoner: Array<Relasjon>
	tmpPersoner?: Array<SivilstandData>
	ident?: string
	identtype?: string
	erRedigerbar?: boolean
}

type VisningData = {
	sivilstandData: SivilstandData
	relasjoner: Array<Relasjon>
	redigertRelatertePersoner?: Array<Relasjon> | null
	idx: number
	data?: Array<SivilstandData>
	tmpPersoner?: Array<SivilstandData>
	ident?: string
	identtype?: string
}

const SivilstandLes = ({
	sivilstandData,
	redigertRelatertePersoner = null,
	relasjoner,
	idx,
}: VisningData) => {
	if (!sivilstandData) {
		return null
	}

	const relatertPersonIdent = sivilstandData.relatertVedSivilstand
	const relasjon = relasjoner?.find((item) => item.relatertPerson?.ident === relatertPersonIdent)
	const relasjonRedigert = redigertRelatertePersoner?.find(
		(item) => item.relatertPerson?.ident === relatertPersonIdent,
	)

	return (
		<div className="person-visning_redigerbar" key={idx}>
			<ErrorBoundary>
				<div className="person-visning_content">
					<TitleValue title="Type" value={showLabel('sivilstandType', sivilstandData.type)} />
					<TitleValue
						title="Gyldig fra og med"
						value={
							formatDate(sivilstandData.sivilstandsdato) ||
							formatDate(sivilstandData.gyldigFraOgMed)
						}
					/>
					<TitleValue
						title="Bekreftelsesdato"
						value={formatDate(sivilstandData.bekreftelsesdato)}
					/>
					{!relasjoner && !relasjonRedigert && (
						<TitleValue title="Relatert person" value={sivilstandData.relatertVedSivilstand} />
					)}
					<TitleValue title="Master" value={sivilstandData.master} />
				</div>
				{(relasjonRedigert || relasjon) && (
					<RelatertPerson
						data={relasjonRedigert?.relatertPerson || relasjon?.relatertPerson}
						tittel={sivilstandData.type === 'SAMBOER' ? 'Samboer' : 'Ektefelle/partner'}
					/>
				)}
			</ErrorBoundary>
		</div>
	)
}

const SivilstandVisning = ({
	sivilstandData,
	idx,
	data,
	relasjoner,
	tmpPersoner,
	ident,
	identtype,
}: VisningData) => {
	const { gruppeId } = useParams()
	const { identer: gruppeIdenter } = useGruppeIdenter(gruppeId)

	const initSivilstand = Object.assign(_.cloneDeep(getInitialSivilstand()), data[idx])
	let initialValues = { sivilstand: initSivilstand }
	initialValues.sivilstand.nyRelatertPerson = initialPdlPerson

	const redigertSivilstandPdlf = _.get(tmpPersoner, `${ident}.person.sivilstand`)?.find(
		(a) => a.id === sivilstandData.id,
	)
	const redigertRelatertePersoner = _.get(tmpPersoner, `${ident}.relasjoner`)

	const slettetSivilstandPdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertSivilstandPdlf
	if (slettetSivilstandPdlf) {
		return <OpplysningSlettet />
	}

	const sivilstandValues = redigertSivilstandPdlf ? redigertSivilstandPdlf : sivilstandData
	let redigertSivilstandValues = redigertSivilstandPdlf
		? {
				sivilstand: Object.assign(_.cloneDeep(getInitialSivilstand()), redigertSivilstandPdlf),
			}
		: null
	if (redigertSivilstandValues) {
		redigertSivilstandValues.sivilstand.nyRelatertPerson = initialPdlPerson
	}

	const eksisterendeNyPerson = redigertRelatertePersoner
		? getEksisterendeNyPerson(redigertRelatertePersoner, sivilstandValues?.relatertVedSivilstand, [
				'EKTEFELLE_PARTNER',
			])
		: getEksisterendeNyPerson(relasjoner, sivilstandValues?.relatertVedSivilstand, [
				'EKTEFELLE_PARTNER',
			])

	const erIGruppe = gruppeIdenter?.some(
		(person) => person.ident === initialValues?.sivilstand?.relatertVedSivilstand,
	)
	const relatertPersonInfo = erIGruppe
		? {
				ident: initialValues?.sivilstand?.relatertVedSivilstand,
			}
		: null

	return (
		<VisningRedigerbarConnector
			dataVisning={
				<SivilstandLes
					sivilstandData={sivilstandValues}
					redigertRelatertePersoner={redigertRelatertePersoner}
					relasjoner={relasjoner}
					idx={idx}
				/>
			}
			initialValues={initialValues}
			eksisterendeNyPerson={eksisterendeNyPerson}
			redigertAttributt={redigertSivilstandValues}
			path="sivilstand"
			ident={ident}
			relatertPersonInfo={relatertPersonInfo}
			identtype={identtype}
		/>
	)
}

export const Sivilstand = ({
	data,
	relasjoner,
	tmpPersoner,
	ident,
	identtype,
	erRedigerbar = true,
}: SivilstandTypes) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<div>
			<SubOverskrift label="Sivilstand (partner)" iconKind="partner" />
			<div className="person-visning_content">
				<DollyFieldArray data={data} nested>
					{(sivilstand: SivilstandData, idx: number) =>
						erRedigerbar ? (
							<SivilstandVisning
								sivilstandData={sivilstand}
								idx={idx}
								data={data}
								relasjoner={relasjoner}
								tmpPersoner={tmpPersoner}
								ident={ident}
								identtype={identtype}
							/>
						) : (
							<SivilstandLes sivilstandData={sivilstand} relasjoner={relasjoner} idx={idx} />
						)
					}
				</DollyFieldArray>
			</div>
		</div>
	)
}
