import React from 'react'
import { NavnForm } from '~/components/fagsystem/pdlf/form/partials/navn/Navn'
import { KjoennForm } from '~/components/fagsystem/pdlf/form/partials/kjoenn/Kjoenn'
import { PersonstatusForm } from '~/components/fagsystem/pdlf/form/partials/personstatus/Personstatus'
import { FormikProps } from 'formik'
import { Alert } from '@navikt/ds-react'
import { Hjelpetekst } from '~/components/hjelpetekst/Hjelpetekst'

type PersondetaljerSamletTypes = {
	formikBag: FormikProps<{}>
	tpsMessaging?: {
		tpsMessagingData: {
			sprakKode: string
		}
	}
	harSkjerming?: string
}

export const PersondetaljerSamlet = ({
	formikBag,
	tpsMessaging,
	harSkjerming,
}: PersondetaljerSamletTypes) => {
	const sprak = tpsMessaging?.tpsMessagingData?.sprakKode

	const getTekst = () => {
		if (sprak) {
			if (harSkjerming) {
				return ', språk og skjerming'
			} else {
				return ' og språk'
			}
		} else if (harSkjerming) {
			return ' og skjerming'
		} else {
			return ''
		}
	}

	return (
		<>
			<div className="flexbox--full-width">
				<Alert variant={'info'}>{`Identnummer${getTekst()} kan ikke endres her.`}</Alert>

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
