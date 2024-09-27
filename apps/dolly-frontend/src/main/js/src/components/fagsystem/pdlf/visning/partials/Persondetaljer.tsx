import React from 'react'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import _ from 'lodash'
import {
	getInitialKjoenn,
	getInitialNavn,
	initialPersonstatus,
} from '@/components/fagsystem/pdlf/form/initialValues'
import VisningRedigerbarPersondetaljerConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarPersondetaljerConnector'
import { TpsMPersonInfo } from '@/components/fagsystem/pdl/visning/partials/tpsMessaging/TpsMPersonInfo'
import { PersonData } from '@/components/fagsystem/pdlf/PdlTypes'
import { SkjermingVisning } from '@/components/fagsystem/skjermingsregister/visning/Visning'
import { Skjerming } from '@/components/fagsystem/skjermingsregister/SkjermingTypes'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'
import { OpplysningSlettet } from '@/components/fagsystem/pdlf/visning/visningRedigerbar/OpplysningSlettet'
import { ArrayHistorikk } from '@/components/ui/historikk/ArrayHistorikk'

type PersondetaljerTypes = {
	data: any
	tmpPersoner: any
	ident: string
	erPdlVisning: boolean
	tpsMessaging: any
	tpsMessagingLoading?: boolean
	skjermingData: Skjerming
	erRedigerbar?: boolean
}

type PersonTypes = {
	person: PersonData
	skjerming?: Skjerming
	redigertPerson: any
	tpsMessaging: any
	tpsMessagingLoading?: boolean
}

const Personstatus = (personstatus) => {
	return (
		<>
			<TitleValue title="Status" value={showLabel('personstatus', personstatus?.data?.status)} />
			<TitleValue
				title="Gyldig fra og med"
				value={formatDate(personstatus?.data?.gyldigFraOgMed)}
			/>
			<TitleValue
				title="Gyldig til og med"
				value={formatDate(personstatus?.data?.gyldigTilOgMed)}
			/>
		</>
	)
}

const PersonstatusVisning = ({ personstatusListe }) => {
	if (!personstatusListe || personstatusListe?.length === 0) {
		return null
	}

	if (personstatusListe?.length === 1) {
		return (
			<TitleValue
				title="Personstatus"
				value={showLabel('personstatus', personstatusListe[0].status)}
			/>
		)
	}

	const gyldigPersonstatus = personstatusListe[0]
	const historiskePersonstatuser = personstatusListe.slice(1)

	return (
		<ArrayHistorikk
			component={Personstatus}
			data={[gyldigPersonstatus]}
			historiskData={historiskePersonstatuser}
			header="Personstatus"
		/>
	)
}

const NavnVisning = ({ navn, showMaster }) => {
	if (!navn) {
		return null
	}

	return (
		<>
			<TitleValue title="Fornavn" value={navn.fornavn} />
			<TitleValue title="Mellomnavn" value={navn.mellomnavn} />
			<TitleValue title="Etternavn" value={navn.etternavn} />
			<TitleValue title="Navn gyldig f.o.m." value={formatDate(navn.gyldigFraOgMed)} />
			<TitleValue title="Master" value={navn.master} hidden={!showMaster} />
		</>
	)
}

const PersondetaljerLes = ({
	person,
	skjerming,
	redigertPerson,
	tpsMessaging,
	tpsMessagingLoading,
}: PersonTypes) => {
	const navnListe = person?.navn
	const personKjoenn = person?.kjoenn?.[0]
	const personstatus = person?.folkeregisterPersonstatus

	return (
		<div className="person-visning_redigerbar">
			<TitleValue title="Ident" value={person?.ident} />
			{navnListe?.length === 1 && <NavnVisning navn={navnListe[0]} />}
			<TitleValue title="Kjønn" value={personKjoenn?.kjoenn} />
			{personstatus?.length > 1 ? (
				<PersonstatusVisning
					personstatusListe={
						redigertPerson?.folkeregisterPersonstatus || person?.folkeregisterPersonstatus
					}
				/>
			) : (
				<TitleValue
					title="Personstatus"
					value={showLabel('personstatus', personstatus?.[0]?.status)}
				/>
			)}
			<SkjermingVisning data={skjerming} />
			<TpsMPersonInfo data={tpsMessaging} loading={tpsMessagingLoading} />
		</div>
	)
}

export const Persondetaljer = ({
	data,
	tmpPersoner,
	ident,
	erPdlVisning = false,
	tpsMessaging,
	tpsMessagingLoading = false,
	skjermingData,
	erRedigerbar = true,
}: PersondetaljerTypes) => {
	if (!data) {
		return null
	}
	const redigertPerson = _.get(tmpPersoner?.pdlforvalter, `${data?.ident}.person`)

	const getNavn = () => {
		if (data?.navn?.length > 1) {
			return undefined
		} else if (data?.navn?.length === 1) {
			return [data.navn[0]]
		} else return [getInitialNavn()]
	}

	const getPersonstatus = () => {
		if (data?.identtype === 'NPID') {
			return undefined
		}
		return [data?.folkeregisterPersonstatus?.[0] || initialPersonstatus]
	}

	const initPerson = {
		navn: getNavn(),
		kjoenn: [data?.kjoenn?.[0] || getInitialKjoenn()],
		folkeregisterpersonstatus: getPersonstatus(),
		skjermingsregister: skjermingData,
	}

	const redigertPersonPdlf = _.get(tmpPersoner?.pdlforvalter, `${ident}.person`)
	const redigertSkjerming = _.get(tmpPersoner?.skjermingsregister, `${ident}`)

	const personValues = redigertPersonPdlf ? redigertPersonPdlf : data
	const redigertPdlfPersonValues = redigertPersonPdlf
		? {
				navn: [redigertPersonPdlf?.navn ? redigertPersonPdlf?.navn?.[0] : getInitialNavn()],
				kjoenn: [redigertPersonPdlf?.kjoenn ? redigertPersonPdlf?.kjoenn?.[0] : getInitialKjoenn()],
				folkeregisterpersonstatus: [
					redigertPersonPdlf?.folkeregisterPersonstatus
						? redigertPersonPdlf?.folkeregisterPersonstatus?.[0]
						: initialPersonstatus,
				],
			}
		: null

	const redigertPersonValues = {
		pdlf: redigertPdlfPersonValues,
		skjermingsregister: redigertSkjerming ? redigertSkjerming : null,
	}

	const tmpNavn = _.get(tmpPersoner?.pdlforvalter, `${ident}.person.navn`)

	return (
		<ErrorBoundary>
			<div>
				<SubOverskrift label="Persondetaljer" iconKind="personinformasjon" />
				<div className="person-visning_content" style={{ flexDirection: 'column' }}>
					{erPdlVisning || !erRedigerbar ? (
						<PersondetaljerLes
							person={data}
							skjerming={skjermingData}
							redigertPerson={redigertPerson}
							tpsMessaging={tpsMessaging}
							tpsMessagingLoading={tpsMessagingLoading}
						/>
					) : (
						<>
							<VisningRedigerbarPersondetaljerConnector
								dataVisning={
									<PersondetaljerLes
										person={personValues}
										skjerming={redigertSkjerming ? redigertSkjerming : skjermingData}
										redigertPerson={redigertPerson}
										tpsMessaging={tpsMessaging}
										tpsMessagingLoading={tpsMessagingLoading}
									/>
								}
								initialValues={initPerson}
								redigertAttributt={redigertPersonValues}
								path="person"
								ident={ident}
								tpsMessagingData={tpsMessaging}
								identtype={data?.identtype}
							/>
							{(tmpNavn ? tmpNavn.length > 1 : data?.navn?.length > 1) && (
								<DollyFieldArray data={data?.navn} header="Navn" nested>
									{(navn) => {
										const redigertNavn = _.get(
											tmpPersoner?.pdlforvalter,
											`${ident}.person.navn`,
										)?.find((a) => a.id === navn.id)

										const slettetNavn =
											tmpPersoner?.pdlforvalter?.hasOwnProperty(ident) && !redigertNavn
										if (slettetNavn) {
											return <OpplysningSlettet />
										}

										const filtrertNavn = tmpNavn
											? tmpNavn?.filter((navnData) => navnData?.id !== navn?.id)
											: data?.navn?.filter((navnData) => navnData?.id !== navn?.id)

										const navnFoerLeggTil = {
											pdlforvalter: {
												person: {
													navn: filtrertNavn,
												},
											},
										}

										return (
											<VisningRedigerbarConnector
												dataVisning={
													<NavnVisning
														navn={redigertNavn ? redigertNavn : navn}
														showMaster={true}
													/>
												}
												initialValues={{ navn: navn }}
												redigertAttributt={redigertNavn && { navn: redigertNavn }}
												path="navn"
												ident={ident}
												tpsMessagingData={tpsMessaging}
												personFoerLeggTil={navnFoerLeggTil}
											/>
										)
									}}
								</DollyFieldArray>
							)}
						</>
					)}
				</div>
			</div>
		</ErrorBoundary>
	)
}
