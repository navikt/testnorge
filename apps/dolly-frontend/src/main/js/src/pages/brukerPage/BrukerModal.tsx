import React, { useEffect, useState } from 'react'
import Loading from '@/components/ui/loading/Loading'
import BrukernavnVelger from '@/pages/brukerPage/BrukernavnVelger'
import OrganisasjonVelger from '@/pages/brukerPage/OrganisasjonVelger'
import { Bruker, Organisasjon, OrgResponse } from '@/pages/brukerPage/types'
import { BrukerApi, PersonOrgTilgangApi, SessionApi } from '@/service/Api'
import { NotFoundError } from '@/error'
import { Navigate } from 'react-router'
import { Logger } from '@/logger/Logger'
import logoutBruker from '@/components/utlogging/logoutBruker'
import { LogoutErrorStates } from '@/components/utlogging/logoutState'

export default () => {
	const [loading, setLoading] = useState(true)
	const [brukerResponse, setBrukerResponse] = useState<Bruker | null>(null)
	const [organisasjoner, setOrganisasjoner] = useState<Organisasjon[]>([])
	const [organisasjon, setOrganisasjon] = useState<Organisasjon | null>(null)
	const [modalHeight, setModalHeight] = useState(310)
	const [sessionUpdated, setSessionUpdated] = useState(false)

	useEffect(() => {
		PersonOrgTilgangApi.getOrganisasjoner()
			.then((response: OrgResponse) => {
				if (!response?.data?.length) {
					Logger.error({
						event: 'Fant ingen organisasjoner for BankID-bruker',
						message: 'Fant ingen organisasjoner for BankID-bruker',
						uuid: window.uuid,
					})
					logoutBruker(LogoutErrorStates.ORGANISATION_ERROR)
					return
				}
				setOrganisasjoner(response.data)
				setModalHeight(310 + 55 * response.data.length)
				setLoading(false)
			})
			.catch(() => {
				Logger.error({
					event: 'Klarte ikke å hente organisasjoner for BankID-bruker',
					message: 'Klarte ikke å hente organisasjoner for BankID-bruker',
					uuid: window.uuid,
				})
				logoutBruker(LogoutErrorStates.PERSON_ORG_ERROR)
			})
	}, [])

	const selectOrganisasjon = (org: Organisasjon) => {
		setLoading(true)
		setOrganisasjon(org)
		setModalHeight(620)
		BrukerApi.getBruker(org.organisasjonsnummer)
			.then((response: Bruker) => {
				if (!response) {
					setLoading(false)
					return
				}
				Logger.trace({
					event: 'Bruker funnet i bruker-service',
					message: `Bruker ${response.brukernavn}, ${response.epost}, som representerer org: ${response.organisasjonsnummer} funnet i bruker-service`,
					uuid: window.uuid,
				})
				setBrukerResponse(response)
				setLoading(false)
				if (response.epost) {
					addToSession(org.organisasjonsnummer)
				}
			})
			.catch((error: unknown) => {
				if (error instanceof NotFoundError) {
					setLoading(false)
					return
				}
				Logger.error({
					event: 'Klarte ikke å hente BankID-bruker fra bruker-service',
					message: 'Klarte ikke å hente BankID-bruker fra bruker-service',
					uuid: window.uuid,
				})
				logoutBruker(LogoutErrorStates.UNKNOWN_ERROR)
			})
	}

	const addToSession = (org: string) => {
		SessionApi.addToSession(org)
			.then(() => setSessionUpdated(true))
			.catch(() => {
				Logger.error({
					event: 'Klarte ikke å sette session for BankID-bruker',
					message: 'Klarte ikke å sette session for BankID-bruker',
					uuid: window.uuid,
				})
				logoutBruker(LogoutErrorStates.SESSION_ERROR)
			})
	}

	if (sessionUpdated) {
		return <Navigate to={'/'} />
	}

	return (
		<div className="bruker-container">
			<div className="bruker-modal" style={{ height: modalHeight + 'px', display: 'flexbox' }}>
				<h1>Velkommen til Dolly</h1>
				{loading && <Loading label="Loading" />}
				{!organisasjon && !loading && (
					<OrganisasjonVelger orgdata={organisasjoner} onClick={selectOrganisasjon} />
				)}
				{organisasjon && !loading && !brukerResponse?.epost && (
					<BrukernavnVelger
						eksisterendeBrukernavn={brukerResponse?.brukernavn}
						organisasjon={organisasjon}
						addToSession={addToSession}
					/>
				)}
			</div>
		</div>
	)
}
