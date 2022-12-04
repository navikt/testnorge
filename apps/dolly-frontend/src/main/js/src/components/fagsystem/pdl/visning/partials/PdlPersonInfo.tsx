import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Formatters from '~/utils/DataFormatter'
import { TpsMPersonInfo } from '~/components/fagsystem/pdl/visning/partials/tpsMessaging/TpsMPersonInfo'
import _get from 'lodash/get'

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

export const PdlPersonInfo = ({
	data,
	tpsMessagingData,
	tpsMessagingLoading = false,
	visTittel = true,
	tmpPersoner = null,
}) => {
	if (!data) {
		return null
	}
	const redigertPerson = _get(tmpPersoner, `${data?.ident}.person`)
	const personNavn = data?.navn?.[0]
	const personKjoenn = data?.kjoenn?.[0]
	const personstatus = getCurrentPersonstatus(redigertPerson || data)

	if (
		!data?.ident &&
		!personNavn &&
		!personKjoenn &&
		!personstatus &&
		!tpsMessagingData &&
		!tpsMessagingLoading
	) {
		return null
	}

	return (
		<ErrorBoundary>
			<div>
				{visTittel && <SubOverskrift label="Persondetaljer" iconKind="personinformasjon" />}
				<div className="person-visning_content">
					<TitleValue title="Ident" value={data?.ident} />
					<TitleValue title="Fornavn" value={personNavn?.fornavn} />
					<TitleValue title="Mellomnavn" value={personNavn?.mellomnavn} />
					<TitleValue title="Etternavn" value={personNavn?.etternavn} />
					<TitleValue title="Kjønn" value={personKjoenn?.kjoenn} />
					<TitleValue
						title="Personstatus"
						value={Formatters.showLabel('personstatus', personstatus?.status)}
					/>
					<TpsMPersonInfo data={tpsMessagingData} loading={tpsMessagingLoading} />
				</div>
			</div>
		</ErrorBoundary>
	)
}
