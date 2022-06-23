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
import { TpsMessagingData } from '~/components/fagsystem/tpsmessaging/form/TpsMessagingData'
import { TpsMPersonInfo } from '~/components/fagsystem/pdl/visning/partials/tpsMessaging/TpsMPersonInfo'

const getCurrentPersonstatus = (data) => {
	if (data?.folkeregisterpersonstatus && data?.folkeregisterpersonstatus?.[0] !== null) {
		const statuser = data.folkeregisterpersonstatus.filter((status) => {
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
}) => {
	if (!data) {
		return null
	}
	const redigertPerson = _get(tmpPersoner, `${data?.ident}.person`)

	const PersondetaljerLes = ({ person }) => {
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
				<TpsMPersonInfo
					data={tpsMessaging.tpsMessagingData}
					loading={tpsMessaging.tpsMessagingLoading}
				/>
			</div>
		)
	}

	const PersondetaljerVisning = ({ person }) => {
		const initPerson = {
			navn: [data?.navn?.[0] || initialNavn],
			kjoenn: [data?.kjoenn?.[0] || initialKjoenn],
			folkeregisterpersonstatus: [data?.folkeregisterPersonstatus?.[0] || initialPersonstatus],
		}

		const redigertPersonPdlf = _get(tmpPersoner, `${ident}.person`)

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
			  }
			: null

		return erPdlVisning ? (
			<PersondetaljerLes person={person} />
		) : (
			<VisningRedigerbarPersondetaljerConnector
				dataVisning={<PersondetaljerLes person={personValues} />}
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
