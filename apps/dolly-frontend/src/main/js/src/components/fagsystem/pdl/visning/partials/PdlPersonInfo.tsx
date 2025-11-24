import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import React from 'react'
import { ArrayHistorikk } from '@/components/ui/historikk/ArrayHistorikk'

const PdlPersonstatus = ({ data }) => {
	return (
		<>
			<TitleValue title="Status" value={showLabel('personstatus', data?.status)} />
			<TitleValue
				title="Gyldig fra og med"
				value={formatDate(data?.folkeregistermetadata?.gyldighetstidspunkt)}
			/>
			<TitleValue
				title="Gyldig til og med"
				value={formatDate(data?.folkeregistermetadata?.opphoerstidspunkt)}
			/>
		</>
	)
}

const PdlNavnVisning = ({ data, showMaster }) => {
	if (!data) {
		return null
	}

	return (
		<>
			<TitleValue title="Fornavn" value={data.fornavn} />
			<TitleValue title="Mellomnavn" value={data.mellomnavn} />
			<TitleValue title="Etternavn" value={data.etternavn} />
			<TitleValue title="Navn gyldig f.o.m." value={formatDate(data.gyldigFraOgMed)} />
			<TitleValue title="Master" value={data.metadata?.master} hidden={!showMaster} />
		</>
	)
}

export const PdlPersonInfo = ({ data, tpsMessagingData, tpsMessagingLoading = false }) => {
	if (!data) {
		return null
	}

	const personNavn = data?.navn?.[0]
	const personKjoenn = data?.kjoenn?.[0]

	const personstatus = data?.folkeregisterpersonstatus
	const gyldigPersonstatus = personstatus?.filter((status) => !status.metadata?.historisk)
	const historiskPersonstatus = personstatus?.filter((status) => status.metadata?.historisk)

	const gyldigeNavn = data?.navn?.filter((navn) => !navn.metadata?.historisk)
	const historiskeNavn = data?.navn?.filter((navn) => navn.metadata?.historisk)

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
				<SubOverskrift label="Persondetaljer" iconKind="personinformasjon" />
				<div className="person-visning_content">
					<TitleValue title="Ident" value={data?.ident} />
					{gyldigeNavn?.length === 1 && (!historiskeNavn || historiskeNavn.length < 1) && (
						<PdlNavnVisning data={gyldigeNavn[0]} showMaster={false} />
					)}
					<TitleValue title="Kjønn" value={personKjoenn?.kjoenn} />
					{(gyldigPersonstatus?.length > 1 || historiskPersonstatus?.length > 0) && (
						<ArrayHistorikk
							component={PdlPersonstatus}
							data={gyldigPersonstatus}
							historiskData={historiskPersonstatus}
							header="Personstatus"
							showMaster={true}
						/>
					)}
					{gyldigPersonstatus?.length === 1 &&
						(!historiskPersonstatus || historiskPersonstatus?.length < 1) && (
							<TitleValue
								title="Personstatus"
								value={showLabel('personstatus', gyldigPersonstatus?.[0]?.status)}
							/>
						)}
					{(gyldigeNavn?.length > 1 || historiskeNavn?.length > 0) && (
						<ArrayHistorikk
							component={PdlNavnVisning}
							data={gyldigeNavn}
							historiskData={historiskeNavn}
							header="Navn"
							showMaster={true}
						/>
					)}
				</div>
			</div>
		</ErrorBoundary>
	)
}
