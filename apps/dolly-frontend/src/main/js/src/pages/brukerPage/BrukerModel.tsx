import React, { useEffect, useState } from 'react'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import Loading from '~/components/ui/loading/Loading'
import BrukernavnVelger from '~/pages/brukerPage/BrukernavnVelger'
import OrganisasjonVelger from '~/pages/brukerPage/OrganisasjonVelger'
import { Bruker, Organisasjon, OrgResponse } from '~/pages/brukerPage/types'
import { BrukerApi, PersonOrgTilgangApi, SessionApi } from '~/service/Api'
import { logoutBruker } from '~/components/utlogging/Utlogging'
import { useNavigate } from 'react-router-dom'

const ORG_ERROR = 'organisation_error'
const UNKNOWN_ERROR = 'unknown_error'

export default () => {
	const [loading, setLoading] = useState(true)
	const [organisasjoner, setOrganisasjoner] = useState([])
	const [organisasjon, setOrganisasjon] = useState<Organisasjon>(null)
	const [modalHeight, setModalHeight] = useState(310)
	const [sessionUpdated, setSessionUpdated] = useState(false)
	const navigate = useNavigate()

	useEffect(() => {
		PersonOrgTilgangApi.getOrganisasjoner()
			.then((response: OrgResponse) => {
				if (response === null || response.data === null || response.data.length === 0) {
					logoutBruker(navigate, UNKNOWN_ERROR)
				}
				setOrganisasjoner(response.data)
				setModalHeight(310 + 55 * response.data.length)
				setLoading(false)
			})
			.catch(() => logoutBruker(navigate, ORG_ERROR))
			.catch(() => logoutBruker(navigate, UNKNOWN_ERROR))
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
					logoutBruker(navigate, UNKNOWN_ERROR)
				}
			})
			.catch(() => {
				setLoading(false)
			})
			.catch(() => {
				logoutBruker(navigate, UNKNOWN_ERROR)
			})
	}

	const addToSession = (org: string) => {
		SessionApi.addToSession(org).then(() => setSessionUpdated(true))
	}

	if (sessionUpdated) return navigate('/')

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
				<NavButton className="tilbake-button" onClick={() => logoutBruker(navigate)}>
					Tilbake til innlogging
				</NavButton>
			</div>
		</div>
	)
}
