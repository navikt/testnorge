import React, { useState, useEffect } from 'react'
import { NotFoundError } from '~/error'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import Loading from '~/components/ui/loading/Loading'
import BrukernavnVelger from '~/pages/brukerPage/BrukernavnVelger'
import OrganisasjonVelger from '~/pages/brukerPage/OrganisasjonVelger'
import { Bruker, OrgResponse, Organisasjon } from '~/pages/brukerPage/types'
import { PersonOrgTilgangApi, BrukerApi, SessionApi } from '~/service/Api'
import { Redirect } from 'react-router-dom'

const logout = () => (window.location.href = '/logout')

export default () => {
	const [loading, setLoading] = useState(true)
	const [organisasjoner, setOrganisasjoner] = useState([])
	const [organisasjon, setOrganisasjon] = useState<Organisasjon>(null)
	const [modalHeight, setModalHeight] = useState(310)
	const [sessionUpdated, setSessionUpdated] = useState(false)

	useEffect(() => {
		PersonOrgTilgangApi.getOrganisasjoner()
			.then((response: OrgResponse) => {
				if (response === null || response.data === null || response.data.length === 0) {
					logout()
				}

				setOrganisasjoner(response.data)
				setModalHeight(310 + 55 * response.data.length)
				setLoading(false)
			})
			.catch(() => {
				logout()
			})
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
					logout()
				}
			})
			.catch((e: NotFoundError) => {
				setLoading(false)
			})
			.catch((e: Error) => {
				logout()
			})
	}

	const addToSession = (org: string) => {
		SessionApi.addToSession(org).then(() => setSessionUpdated(true))
	}

	if (sessionUpdated) return <Redirect to={''} />

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
				<NavButton className="tilbake-button" onClick={() => logout()}>
					Tilbake til innlogging
				</NavButton>
			</div>
		</div>
	)
}
