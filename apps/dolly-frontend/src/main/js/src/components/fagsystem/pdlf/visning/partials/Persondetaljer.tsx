import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Formatters from '~/utils/DataFormatter'
import _get from 'lodash/get'
import {
	initialKjoenn,
	initialNavn,
	initialPersonstatus,
} from '~/components/fagsystem/pdlf/form/initialValues'
import VisningRedigerbarPersondetaljerConnector from '~/components/fagsystem/pdlf/visning/VisningRedigerbarPersondetaljerConnector'
import { TpsMPersonInfo } from '~/components/fagsystem/pdl/visning/partials/tpsMessaging/TpsMPersonInfo'
import { PersonData } from '~/components/fagsystem/pdlf/PdlTypes'
import { SkjermingVisning } from '~/components/fagsystem/skjermingsregister/visning/Visning'
import { Skjerming } from '~/components/fagsystem/skjermingsregister/SkjermingTypes'

type PersondetaljerTypes = {
	data: any
	tmpPersoner: any
	ident: string
	erPdlVisning: boolean
	tpsMessaging: any
	skjermingData: Skjerming
}

type PersonTypes = {
	person: PersonData
	skjerming?: Skjerming
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

export const Persondetaljer = ({
	data,
	tmpPersoner,
	ident,
	erPdlVisning = false,
	tpsMessaging,
	skjermingData,
}: PersondetaljerTypes) => {
	if (!data) {
		return null
	}
	const redigertPerson = _get(tmpPersoner?.pdlforvalter, `${data?.ident}.person`)

	const PersondetaljerLes = ({ person, skjerming }: PersonTypes) => {
		const personNavn = person?.navn?.[0]
		const personKjoenn = person?.kjoenn?.[0]
		const personstatus = getCurrentPersonstatus(redigertPerson || person)

		return (
			<div className="person-visning_redigerbar">
				<TitleValue title="Ident" value={person?.ident} />
				<TitleValue title="Fornavn" value={personNavn?.fornavn} />
				<TitleValue title="Mellomnavn" value={personNavn?.mellomnavn} />
				<TitleValue title="Etternavn" value={personNavn?.etternavn} />
				<TitleValue title="Kjønn" value={personKjoenn?.kjoenn} />
				<TitleValue
					title="Personstatus"
					value={Formatters.showLabel('personstatus', personstatus?.status)}
				/>
				<SkjermingVisning data={skjerming} />
				<TpsMPersonInfo
					data={tpsMessaging.tpsMessagingData}
					loading={tpsMessaging.tpsMessagingLoading}
				/>
			</div>
		)
	}

	const PersondetaljerVisning = ({ person }: PersonTypes) => {
		const initPerson = {
			navn: [data?.navn?.[0] || initialNavn],
			kjoenn: [data?.kjoenn?.[0] || initialKjoenn],
			folkeregisterpersonstatus: [data?.folkeregisterPersonstatus?.[0] || initialPersonstatus],
			skjermingsregister: skjermingData,
		}

		const redigertPersonPdlf = _get(tmpPersoner?.pdlforvalter, `${ident}.person`)
		const redigertSkjerming = _get(tmpPersoner?.skjermingsregister, `${ident}`)

		const personValues = redigertPersonPdlf ? redigertPersonPdlf : person
		const redigertPersonValues = redigertPersonPdlf
			? {
					navn: [redigertPersonPdlf?.navn ? redigertPersonPdlf?.navn?.[0] : initialNavn],
					kjoenn: [redigertPersonPdlf?.kjoenn ? redigertPersonPdlf?.kjoenn?.[0] : initialKjoenn],
					folkeregisterpersonstatus: [
						redigertPersonPdlf?.folkeregisterPersonstatus
							? redigertPersonPdlf?.folkeregisterPersonstatus?.[0]
							: initialPersonstatus,
					],
					skjermingsregister: redigertSkjerming ? redigertSkjerming : null,
			  }
			: null

		return erPdlVisning ? (
			<PersondetaljerLes person={person} skjerming={skjermingData} />
		) : (
			<VisningRedigerbarPersondetaljerConnector
				dataVisning={
					<PersondetaljerLes
						person={personValues}
						skjerming={redigertSkjerming ? redigertSkjerming : skjermingData}
					/>
				}
				initialValues={initPerson}
				redigertAttributt={redigertPersonValues}
				path="person"
				ident={ident}
				tpsMessagingData={tpsMessaging}
			/>
		)
	}

	return (
		<ErrorBoundary>
			<div>
				<SubOverskrift label="Persondetaljer" iconKind="personinformasjon" />
				<div className="person-visning_content">
					<PersondetaljerVisning person={data} />
				</div>
			</div>
		</ErrorBoundary>
	)
}
