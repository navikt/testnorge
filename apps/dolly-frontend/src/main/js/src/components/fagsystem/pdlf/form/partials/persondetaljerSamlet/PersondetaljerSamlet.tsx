import React from 'react'
import { NavnForm } from '~/components/fagsystem/pdlf/form/partials/navn/Navn'
import { KjoennForm } from '~/components/fagsystem/pdlf/form/partials/kjoenn/Kjoenn'
import { PersonstatusForm } from '~/components/fagsystem/pdlf/form/partials/personstatus/Personstatus'
import { FormikProps } from 'formik'
import { Alert } from '@navikt/ds-react'
import { Hjelpetekst } from '~/components/hjelpetekst/Hjelpetekst'
import { Skjerming } from '~/components/fagsystem/skjermingsregister/SkjermingTypes'

type PersondetaljerSamletTypes = {
	formikBag: FormikProps<{}>
	tpsMessaging?: {
		tpsMessagingData: {
			sprakKode: string
		}
	}
	skjermingData?: Skjerming
}

export const PersondetaljerSamlet = ({
	formikBag,
	tpsMessaging,
	skjermingData,
}: PersondetaljerSamletTypes) => {
	const sprak = tpsMessaging?.tpsMessagingData?.sprakKode
	const skjerming = skjermingData?.skjermetFra

	const sprakTekst = ' og språk'
	const skjermingTekst = ' og skjerming'
	const beggeTekst = ', språk og skjerming'

	return (
		<>
			<div className="flexbox--full-width">
				<Alert variant={'info'}>{`Identnummer${
					sprak && skjerming ? beggeTekst : sprak ? sprakTekst : skjerming ? skjermingTekst : ''
				} kan ikke endres her.`}</Alert>

				<h3>Navn</h3>
				<div className="flexbox--flex-wrap">
					<NavnForm formikBag={formikBag} path="navn[0]" />
				</div>

				<h3>Kjønn</h3>
				<KjoennForm path="kjoenn[0]" />

				<div className="flexbox--align-center">
					<h3>Personstatus</h3>
					<Hjelpetekst>
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
