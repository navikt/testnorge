import React, { useState } from 'react'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import OrganisasjonVelger from '~/pages/brukernavnPage/OrganisasjonVelger'
import BrukernavnVelger from '~/pages/brukernavnPage/BrukernavnVelger'

export type Organisasjon = {
	navn: String
	orgnisasjonsnummer: String
	orgnisasjonsfrom: String
	gyldigTil: String
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
		<div className="brukernavn-container">
			<div className="brukernavn-modal" style={{ height: modalHeight + 'px', display: 'flexbox' }}>
				<h1>Velkommen til Dolly</h1>
				{!organisasjon && <OrganisasjonVelger orgdata={testdata} onClick={setOrganisasjon} />}
				{organisasjon && <BrukernavnVelger organisasjon={organisasjon} />}
				<NavButton className="tilbake-button">Tilbake til innlogging</NavButton>
			</div>
		</div>
	)
}
