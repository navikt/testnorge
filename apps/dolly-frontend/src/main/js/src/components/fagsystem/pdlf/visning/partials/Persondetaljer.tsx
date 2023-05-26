import React from 'react'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import * as _ from 'lodash-es'
import {
	initialKjoenn,
	initialNavn,
	initialPersonstatus,
} from '@/components/fagsystem/pdlf/form/initialValues'
import VisningRedigerbarPersondetaljerConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarPersondetaljerConnector'
import { TpsMPersonInfo } from '@/components/fagsystem/pdl/visning/partials/tpsMessaging/TpsMPersonInfo'
import { PersonData } from '@/components/fagsystem/pdlf/PdlTypes'
import { SkjermingVisning } from '@/components/fagsystem/skjermingsregister/visning/Visning'
import { Skjerming } from '@/components/fagsystem/skjermingsregister/SkjermingTypes'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'

type PersondetaljerTypes = {
	data: any
	tmpPersoner: any
	ident: string
	erPdlVisning: boolean
	tpsMessaging: any
	tpsMessagingLoading?: boolean
	skjermingData: Skjerming
}

type PersonTypes = {
	person: PersonData
	skjerming?: Skjerming
	redigertPerson: any
	tpsMessaging: any
	tpsMessagingLoading?: boolean
}

const getCurrentPersonstatus = (data: any) => {
	if (data?.folkeregisterpersonstatus && data?.folkeregisterpersonstatus?.[0] !== null) {
		const statuser = data.folkeregisterpersonstatus.filter((status: any) => {
			return !status?.metadata?.historisk
		})
		return statuser.length > 0 ? statuser[0] : null
	} else if (data?.folkeregisterPersonstatus && data?.folkeregisterPersonstatus?.[0] !== null) {
		const statuser = data.folkeregisterPersonstatus
		return statuser.length > 0 ? statuser[0] : null
	}
	return null
}

const NavnVisning = ({ navn }) => {
	if (!navn) {
		return null
	}

	return (
		<>
			<TitleValue title="Fornavn" value={navn.fornavn} />
			<TitleValue title="Mellomnavn" value={navn.mellomnavn} />
			<TitleValue title="Etternavn" value={navn.etternavn} />
			<TitleValue
				title="Navn opphørt"
				value={formatDate(navn.folkeregistermetadata?.opphoerstidspunkt)}
			/>
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
	const personstatus = getCurrentPersonstatus(redigertPerson || person)

	return (
		<div className="person-visning_redigerbar">
			<TitleValue title="Ident" value={person?.ident} />
			{navnListe?.length === 1 && <NavnVisning navn={navnListe[0]} />}
			<TitleValue title="Kjønn" value={personKjoenn?.kjoenn} />
			<TitleValue title="Personstatus" value={showLabel('personstatus', personstatus?.status)} />
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
		} else return [initialNavn]
	}

	const initPerson = {
		navn: getNavn(),
		kjoenn: [data?.kjoenn?.[0] || initialKjoenn],
		folkeregisterpersonstatus: [data?.folkeregisterPersonstatus?.[0] || initialPersonstatus],
		skjermingsregister: skjermingData,
	}

	const redigertPersonPdlf = _.get(tmpPersoner?.pdlforvalter, `${ident}.person`)
	const redigertSkjerming = _.get(tmpPersoner?.skjermingsregister, `${ident}`)

	const personValues = redigertPersonPdlf ? redigertPersonPdlf : data
	const redigertPdlfPersonValues = redigertPersonPdlf
		? {
				navn: [redigertPersonPdlf?.navn ? redigertPersonPdlf?.navn?.[0] : initialNavn],
				kjoenn: [redigertPersonPdlf?.kjoenn ? redigertPersonPdlf?.kjoenn?.[0] : initialKjoenn],
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

	const personValuesMedRedigert = _.cloneDeep(data)
	if (tmpNavn && personValuesMedRedigert) {
		personValuesMedRedigert.navn = tmpNavn
	}

	return (
		<ErrorBoundary>
			<div>
				<SubOverskrift label="Persondetaljer" iconKind="personinformasjon" />
				<div className="person-visning_content" style={{ flexDirection: 'column' }}>
					{erPdlVisning ? (
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
							/>
							{(tmpNavn ? tmpNavn.length > 1 : data?.navn?.length > 1) && (
								<DollyFieldArray data={data?.navn} header="Navn" nested>
									{(navn) => {
										const redigertNavn = _.get(
											tmpPersoner?.pdlforvalter,
											`${ident}.person.navn`
										)?.find((a) => a.id === navn.id)

										const slettetNavn =
											tmpPersoner?.pdlforvalter?.hasOwnProperty(ident) && !redigertNavn
										if (slettetNavn) {
											return <pre style={{ margin: '0' }}>Opplysning slettet</pre>
										}

										return (
											<VisningRedigerbarConnector
												dataVisning={<NavnVisning navn={redigertNavn ? redigertNavn : navn} />}
												initialValues={{ navn: navn }}
												redigertAttributt={redigertNavn && { navn: redigertNavn }}
												path="navn"
												ident={ident}
												tpsMessagingData={tpsMessaging}
												personValues={personValuesMedRedigert}
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
