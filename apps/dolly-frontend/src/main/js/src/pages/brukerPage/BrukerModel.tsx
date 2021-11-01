import React, { useState, useEffect } from 'react'
import Api from '~/api'
import { NotFoundError } from '~/error'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import Loading from '~/components/ui/loading/Loading'
import BrukernavnVelger from '~/pages/brukerPage/BrukernavnVelger'
import OrganisasjonVelger from '~/pages/brukerPage/OrganisasjonVelger'
import { Bruker, OrgResponse, Organisasjon } from '~/pages/brukerPage/types'
import { PersonOrgTilgangApi, BrukerApi, SessionApi } from '~/service/Api'
import { Redirect } from 'react-router-dom'

const UNKNOWN_ERROR = 'unknown_error'
const ORGANISATION_ERROR = 'organisation_error'

const logout = (feilmelding: string = null) => {
	Api.fetch('/logout', { method: 'POST' }).then((response) => {
		const redirectUrl = feilmelding ? response.url + '&state=' + feilmelding : response.url
		location.replace(redirectUrl)
	})
}

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
					logout(UNKNOWN_ERROR)
				}

				setOrganisasjoner(response.data)
				setModalHeight(310 + 55 * response.data.length)
				setLoading(false)
			})
			.catch((e: NotFoundError) => {
				logout(ORGANISATION_ERROR)
			})
			.catch((e: Error) => {
				logout(UNKNOWN_ERROR)
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
					logout(UNKNOWN_ERROR)
				}
			})
			.catch((e: NotFoundError) => {
				setLoading(false)
			})
			.catch((e: Error) => {
				logout(UNKNOWN_ERROR)
			})
	}

	const addToSession = (org: string) => {
		SessionApi.addToSession(org)
		setSessionUpdated(true)
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
