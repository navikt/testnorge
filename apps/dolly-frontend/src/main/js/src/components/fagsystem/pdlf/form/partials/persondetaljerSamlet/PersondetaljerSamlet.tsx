import React from 'react'
import { NavnForm } from '~/components/fagsystem/pdlf/form/partials/navn/Navn'
import { KjoennForm } from '~/components/fagsystem/pdlf/form/partials/kjoenn/Kjoenn'
import { PersonstatusForm } from '~/components/fagsystem/pdlf/form/partials/personstatus/Personstatus'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import Hjelpetekst from '~/components/hjelpetekst'

export const PersondetaljerSamlet = ({ formikBag }) => {
	return (
		<>
			<div className="flexbox--full-width">
				<AlertStripeInfo>Identnummer, språk og skjerming kan ikke endres her.</AlertStripeInfo>

				<h3>Navn</h3>
				<div className="flexbox--flex-wrap">
					<NavnForm formikBag={formikBag} path="navn[0]" />
				</div>

				<h3>Kjønn</h3>
				<KjoennForm path="kjoenn[0]" />

				<div className="flexbox--align-center">
					<h3>Personstatus</h3>
					<Hjelpetekst hjelpetekstFor="Personstatus">
						Endring av personstatus er kun ment for negativ testing. Adresser og andre avhengige
						verdier vil ikke bli oppdatert for å stemme overens med ny personstatus.
					</Hjelpetekst>
				</div>
				<div className="flexbox--flex-wrap" style={{ marginTop: '10px' }}>
					<PersonstatusForm path="folkeregisterpersonstatus[0]" />
				</div>
			</div>
		</>
	)
}
