import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import { TpsMPersonInfo } from '@/components/fagsystem/pdl/visning/partials/tpsMessaging/TpsMPersonInfo'
import _ from 'lodash'
import React from 'react'
import { ArrayHistorikk } from '@/components/ui/historikk/ArrayHistorikk'
import { getInitialNavn } from '@/components/fagsystem/pdlf/form/initialValues'
import { OpplysningSlettet } from '@/components/fagsystem/pdlf/visning/visningRedigerbar/OpplysningSlettet'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'

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

const NavnVisningRedigerbar = ({ data, idx, alleData, tmpPersoner, ident, identtype, master }) => {
	const initNavn = Object.assign(_.cloneDeep(getInitialNavn()), alleData[idx])
	const initialValues = { navn: initNavn }

	const redigertNavnPdlf = _.get(tmpPersoner, `${ident}.person.navn`)?.find((a) => a.id === data.id)
	const slettetNavnPdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertNavnPdlf
	if (slettetNavnPdlf) {
		return <OpplysningSlettet />
	}

	const navnValues = redigertNavnPdlf ? redigertNavnPdlf : data
	const redigertNavnValues = redigertNavnPdlf
		? {
				navn: Object.assign(_.cloneDeep(getInitialNavn()), redigertNavnPdlf),
			}
		: null
	return (
		<div className="person-visning_content">
			<VisningRedigerbarConnector
				dataVisning={<PdlNavnVisning data={navnValues} />}
				initialValues={initialValues}
				redigertAttributt={redigertNavnValues}
				path="navn"
				ident={ident}
				identtype={identtype}
			/>
		</div>
	)
}

export const PdlPersonInfo = ({
	data,
	tpsMessagingData,
	tpsMessagingLoading = false,
	pdlfData,
	tmpPersoner = null,
	ident,
	identtype,
	visTittel = true,
}) => {
	if (!data) {
		return null
	}
	const redigertPerson = _.get(tmpPersoner, `${data?.ident}.person`)
	const personNavn = data?.navn?.[0]
	const personKjoenn = data?.kjoenn?.[0]
	const personstatus = getCurrentPersonstatus(redigertPerson || data)

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
				{visTittel && <SubOverskrift label="Persondetaljer" iconKind="personinformasjon" />}
				<div className="person-visning_content">
					<TitleValue title="Ident" value={data?.ident} />
					{gyldigeNavn?.length === 1 && (!historiskeNavn || historiskeNavn.length < 1) && (
						<PdlNavnVisning data={gyldigeNavn[0]} showMaster={false} />
					)}
					<TitleValue title="Kjønn" value={personKjoenn?.kjoenn} />
					<TitleValue
						title="Personstatus"
						value={showLabel('personstatus', personstatus?.status)}
					/>
					{(gyldigeNavn?.length > 1 || historiskeNavn?.length > 0) && (
						<ArrayHistorikk
							component={PdlNavnVisning}
							componentRedigerbar={NavnVisningRedigerbar}
							data={gyldigeNavn}
							pdlfData={pdlfData?.navn}
							historiskData={historiskeNavn}
							tmpPersoner={tmpPersoner}
							ident={ident}
							identtype={identtype}
							header="Navn"
							showMaster={true}
						/>
					)}
					<TpsMPersonInfo data={tpsMessagingData} loading={tpsMessagingLoading} />
				</div>
			</div>
		</ErrorBoundary>
	)
}
