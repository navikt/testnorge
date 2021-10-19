import React, { useState, useEffect } from 'react'
import Api from '~/api'
import { NotFoundError } from '~/error'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import Loading from '~/components/ui/loading/Loading'
import BrukernavnVelger from '~/pages/brukerPage/BrukernavnVelger'
import OrganisasjonVelger from '~/pages/brukerPage/OrganisasjonVelger'
import { BrukerResponse, OrgResponse, Organisasjon } from '~/pages/brukerPage/types'
import { PersonOrgTilgangApi, BrukerApi } from '~/service/Api'

const UNKNOWN_ERROR = 'unknown_error'
const ORGANISATION_ERROR = 'organisation_error'

const logout = (feilmelding: string = 'logout') => {
	let url = window.location.protocol + '//' + window.location.host + '/login?' + feilmelding
	Api.fetch('/logout', { method: 'POST' }).then((response) => window.location.replace(url))
}

const getTokenAndRedirect = (id: string) => {
	BrukerApi.opprettToken(id)
		.then((response: any) => {
			if (response.status === 200) {
				window.location.replace(window.location.protocol + '//' + window.location.host)
			} else {
				logout(UNKNOWN_ERROR)
			}
		})
		.catch(() => {
			logout(UNKNOWN_ERROR)
		})
}

export default () => {
	const [loading, setLoading] = useState(true)
	const [organisasjoner, setOrganisasjoner] = useState([])
	const [organisasjon, setOrganisasjon] = useState<Organisasjon>(null)
	const [modalHeight, setModalHeight] = useState(310)

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
			.catch(() => {
				logout(ORGANISATION_ERROR)
			})
	}, [])

	const selectOrganisasjon = (org: Organisasjon) => {
		setLoading(true)
		setOrganisasjon(org)
		setModalHeight(420)
		BrukerApi.getBrukere(org.organisasjonsnummer)
			.then((response: BrukerResponse) => {
				if (response !== null && response.data !== null && response.data.length !== 0) {
					getTokenAndRedirect(response.data[0].id)
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

	return (
		<div className="bruker-container">
			<div className="bruker-modal" style={{ height: modalHeight + 'px', display: 'flexbox' }}>
				<h1>Velkommen til Dolly</h1>
				{loading && <Loading label="Loading" />}
				{!organisasjon && !loading && (
					<OrganisasjonVelger orgdata={organisasjoner} onClick={selectOrganisasjon} />
				)}
				{organisasjon && !loading && (
					<BrukernavnVelger organisasjon={organisasjon} getTokenAndRedirect={getTokenAndRedirect} />
				)}
				<NavButton className="tilbake-button" onClick={logout}>
					Tilbake til innlogging
				</NavButton>
			</div>
		</div>
	)
}
