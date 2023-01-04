import React, { useEffect, useState } from 'react'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import Loading from '@/components/ui/loading/Loading'
import BrukernavnVelger from '@/pages/brukerPage/BrukernavnVelger'
import OrganisasjonVelger from '@/pages/brukerPage/OrganisasjonVelger'
import { Bruker, Organisasjon, OrgResponse } from '@/pages/brukerPage/types'
import { BrukerApi, PersonOrgTilgangApi, SessionApi } from '@/service/Api'
import { NotFoundError } from '@/error'
import { Navigate } from 'react-router-dom'
import logoutBruker from '@/components/utlogging/logoutBruker'

const ORG_ERROR = 'organisation_error'
const UNKNOWN_ERROR = 'unknown_error'

export default () => {
	const [loading, setLoading] = useState(true)
	const [organisasjoner, setOrganisasjoner] = useState([])
	const [organisasjon, setOrganisasjon] = useState(null)
	const [modalHeight, setModalHeight] = useState(310)
	const [sessionUpdated, setSessionUpdated] = useState(false)

	useEffect(() => {
		PersonOrgTilgangApi.getOrganisasjoner()
			.then((response: OrgResponse) => {
				if (response === null || response.data === null || response.data.length === 0) {
					logoutBruker(UNKNOWN_ERROR)
				}
				setOrganisasjoner(response.data)
				setModalHeight(310 + 55 * response.data.length)
				setLoading(false)
			})
			.catch((_e: NotFoundError) => logoutBruker(ORG_ERROR))
			.catch((_e: Error) => logoutBruker(UNKNOWN_ERROR))
	}, [])

	const selectOrganisasjon = (org: Organisasjon) => {
		setLoading(true)
		setOrganisasjon(org)
		setModalHeight(420)
		BrukerApi.getBruker(org.organisasjonsnummer)
			.then((response: Bruker) => {
				if (response !== null) {
					addToSession(org.organisasjonsnummer)
				} else {
					logoutBruker(UNKNOWN_ERROR)
				}
			})
			.catch((_e: NotFoundError) => {
				setLoading(false)
			})
			.catch((_e: Error) => {
				logoutBruker(UNKNOWN_ERROR)
			})
	}

	const addToSession = (org: string) => {
		SessionApi.addToSession(org).then(() => setSessionUpdated(true))
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
				{organisasjon && !loading && (
					<BrukernavnVelger organisasjon={organisasjon} addToSession={addToSession} />
				)}
				<NavButton className="tilbake-button" onClick={() => logoutBruker()}>
					Tilbake til innlogging
				</NavButton>
			</div>
		</div>
	)
}
