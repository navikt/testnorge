import React from 'react'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import * as _ from 'lodash-es'
import {
	getInitialKjoenn,
	getInitialNavn,
	initialPersonstatus,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { VisningRedigerbarPersondetaljer } from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarPersondetaljer'
import { PersonData } from '@/components/fagsystem/pdlf/PdlTypes'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { VisningRedigerbar } from "@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbar"
import { OpplysningSlettet } from '@/components/fagsystem/pdlf/visning/visningRedigerbar/OpplysningSlettet'
import { Personstatus } from '@/components/fagsystem/pdlf/visning/partials/Personstatus'

type PersondetaljerTypes = {
	data: any
	tmpPersoner: any
	ident: string
	erPdlVisning?: boolean
	tpsMessaging: any
	tpsMessagingLoading?: boolean
	erRedigerbar?: boolean
}

type PersonTypes = {
	person: PersonData
	redigertPerson: any
	tpsMessaging: any
	tpsMessagingLoading?: boolean
	harFlerePersonstatuser?: boolean
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

const PersondetaljerLes = ({ person, redigertPerson, harFlerePersonstatuser }: PersonTypes) => {
	const navnListe = person?.navn
	const personKjoenn = person?.kjoenn?.[0]
	const personstatus =
		redigertPerson?.folkeregisterPersonstatus || person?.folkeregisterPersonstatus

	const getIdenttype = () => {
		if (!person.identtype) return ''
		if (person.id2032) return `${person.identtype} (id 2032)`
		return person.identtype
	}

	return (
		<div className="person-visning_redigerbar">
			<TitleValue title="Ident" value={person?.ident} />
			<TitleValue title="Identtype" value={getIdenttype()} />
			{navnListe?.length === 1 && <NavnVisning navn={navnListe[0]} />}
			<TitleValue title="Kjønn" value={personKjoenn?.kjoenn} />
			{personstatus?.length === 1 && !harFlerePersonstatuser && (
				<>
					<TitleValue
						title="Personstatus"
						value={showLabel('personstatus', personstatus?.[0]?.status)}
					/>
					<TitleValue
						title="Status gyldig f.o.m."
						value={formatDate(personstatus?.[0]?.gyldigFraOgMed)}
					/>
					<TitleValue
						title="Status gyldig t.o.m."
						value={formatDate(personstatus?.[0]?.gyldigTilOgMed)}
					/>
				</>
			)}
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
		if (data?.identtype === 'NPID' || data?.folkeregisterPersonstatus?.length > 1) {
			return undefined
		}
		return [data?.folkeregisterPersonstatus?.[0] || initialPersonstatus]
	}

	const initPerson = {
		navn: getNavn(),
		kjoenn: [data?.kjoenn?.[0] || getInitialKjoenn()],
		folkeregisterpersonstatus: getPersonstatus(),
	}

	const redigertPersonPdlf = _.get(tmpPersoner?.pdlforvalter, `${ident}.person`)

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
	}

	const tmpNavn = _.get(tmpPersoner?.pdlforvalter, `${ident}.person.navn`)

	const tmpPersonstatus = _.get(
		tmpPersoner?.pdlforvalter,
		`${ident}.person.folkeregisterPersonstatus`,
	)

	const harFlerePersonstatuser =
		tmpPersonstatus?.length > 1 || data?.folkeregisterPersonstatus?.length > 1

	return (
		<ErrorBoundary>
			<div>
				<SubOverskrift label="Persondetaljer" iconKind="personinformasjon" />
				<div className="person-visning_content" style={{ flexDirection: 'column' }}>
					{erPdlVisning || !erRedigerbar ? (
						<PersondetaljerLes
							person={data}
							redigertPerson={redigertPerson}
							tpsMessaging={tpsMessaging}
							tpsMessagingLoading={tpsMessagingLoading}
							harFlerePersonstatuser={harFlerePersonstatuser}
						/>
					) : (
						<>
							<VisningRedigerbarPersondetaljer
								dataVisning={
									<PersondetaljerLes
										person={personValues}
										redigertPerson={redigertPerson}
										tpsMessaging={tpsMessaging}
										tpsMessagingLoading={tpsMessagingLoading}
										harFlerePersonstatuser={harFlerePersonstatuser}
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
											<VisningRedigerbar
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
							{harFlerePersonstatuser && (
								<Personstatus
									data={data?.folkeregisterPersonstatus}
									tmpPersoner={tmpPersoner?.pdlforvalter}
									ident={ident}
								/>
							)}
						</>
					)}
				</div>
			</div>
		</ErrorBoundary>
	)
}
