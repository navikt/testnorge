import React, { useState } from 'react'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import OrganisasjonVelger from '~/pages/brukerPage/OrganisasjonVelger'
import BrukernavnVelger from '~/pages/brukerPage/BrukernavnVelger'

export type Organisasjon = {
	navn: string
	orgnisasjonsnummer: string
	orgnisasjonsfrom: string
	gyldigTil: string
}

export default () => {
	const [organisasjon, setOrganisasjon] = useState<Organisasjon>(null)

	let testdata = [
		{
			navn: 'Sau- og ulldepartementet',
			orgnisasjonsnummer: '',
			orgnisasjonsfrom: '',
			gyldigTil: '',
		},
		{ navn: 'Farikåletaten', orgnisasjonsnummer: '', orgnisasjonsfrom: '', gyldigTil: '' },
	]

	let modalHeight = organisasjon ? 420 : 310 + 55 * testdata.length

	return (
		<div className="bruker-container">
			<div className="bruker-modal" style={{ height: modalHeight + 'px', display: 'flexbox' }}>
				<h1>Velkommen til Dolly</h1>
				{!organisasjon && <OrganisasjonVelger orgdata={testdata} onClick={setOrganisasjon} />}
				{organisasjon && <BrukernavnVelger organisasjon={organisasjon} />}
				<NavButton className="tilbake-button">Tilbake til innlogging</NavButton>
			</div>
		</div>
	)
}
