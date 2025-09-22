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
import useBoolean from '@/utils/hooks/useBoolean'
import { ErrorModal } from '@/pages/brukerPage/ErrorModal'

const ORG_ERROR = 'organisation_error'
const UNKNOWN_ERROR = 'unknown_error'

export default () => {
	const [loading, setLoading] = useState(true)
	const [brukerResponse, setBrukerResponse] = useState(null as Bruker | null)
	const [organisasjoner, setOrganisasjoner] = useState([])
	const [organisasjon, setOrganisasjon] = useState(null)
	const [modalHeight, setModalHeight] = useState(310)
	const [sessionUpdated, setSessionUpdated] = useState(false)

	const [errorModalIsOpen, openErrorModal, closeErrorModal] = useBoolean(false)

	useEffect(() => {
		PersonOrgTilgangApi.getOrganisasjoner()
			.then((response: OrgResponse) => {
				if (response === null || response.data === null || response.data.length === 0) {
					Logger.error({
						event: 'Ukjent feil ved henting av organisasjoner for bankid bruker',
						message: 'Ukjent feil ved henting av organisasjoner for bankid bruker',
						uuid: window.uuid,
					})
					openErrorModal()
				}
				setOrganisasjoner(response.data)
				setModalHeight(310 + 55 * response.data.length)
				setLoading(false)
			})
			.catch((_e: NotFoundError) => {
				Logger.error({
					event: 'Fant ingen organisasjoner for bankid bruker',
					message: 'Fant ingen organisasjoner for bankid bruker',
					uuid: window.uuid,
				})
				logoutBruker(ORG_ERROR)
			})
			.catch((e: Error) => {
				Logger.error({
					event: e.name,
					message: e.message,
					uuid: window.uuid,
				})
				logoutBruker(UNKNOWN_ERROR)
			})
	}, [])

	const selectOrganisasjon = (org: Organisasjon) => {
		setLoading(true)
		setOrganisasjon(org)
		setModalHeight(620)
		BrukerApi.getBruker(org.organisasjonsnummer)
			.then((response: Bruker) => {
				if (response !== null) {
					Logger.trace({
						event: 'Bruker funnet i bruker-service',
						message: `Bruker ${response.brukernavn}, ${response.epost}, som representerer org: ${response.organisasjonsnummer} funnet i bruker-service`,
						uuid: window.uuid,
					})
					setBrukerResponse(response)
					setLoading(false)
					// if (response.epost) {
					addToSession(org.organisasjonsnummer)
					// }
				} else {
					Logger.error({
						event: 'Ukjent feil ved henting av bankid bruker fra bruker-service',
						message: 'Ukjent feil ved henting av bankid bruker fra bruker-service',
						uuid: window.uuid,
					})
					logoutBruker(UNKNOWN_ERROR)
				}
			})
			.catch((_e: NotFoundError) => {
				setLoading(false)
			})
			.catch((e: Error) => {
				Logger.error({
					event: e.name,
					message: e.message,
					uuid: window.uuid,
				})
				logoutBruker(UNKNOWN_ERROR)
			})
	}

	const addToSession = (org: string) => {
		SessionApi.addToSession(org)
			.then(() => setSessionUpdated(true))
			.catch(() => {
				Logger.error({
					event: 'Klarte ikke å sette session for bankid bruker',
					message: 'Klarte ikke å sette session for bankid bruker',
					uuid: window.uuid,
				})
				setSessionUpdated(false)
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
				<ErrorModal
					closeErrorModal={closeErrorModal}
					errorModalIsOpen={errorModalIsOpen}
					error={UNKNOWN_ERROR}
				/>
				{!organisasjon && !loading && (
					<OrganisasjonVelger orgdata={organisasjoner} onClick={selectOrganisasjon} />
				)}
				{/*{organisasjon && !loading && !brukerResponse?.epost && (*/}
				{organisasjon && !loading && (
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
