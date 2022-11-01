import React, { useEffect } from 'react'
import _get from 'lodash/get'
import { ifPresent } from '~/utils/YupValidations'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '~/components/ui/form/formUtils'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { MedServicebehov } from './partials/MedServicebehov'
import { AlertInntektskomponentenRequired } from '~/components/ui/brukerAlert/AlertInntektskomponentenRequired'
import { validation } from '~/components/fagsystem/arena/form/validation'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { Alert } from '@navikt/ds-react'

export const arenaPath = 'arenaforvalter'

const errorPaths = [
	`${arenaPath}.dagpenger[0].tilDato`,
	`${arenaPath}.aap[0].tilDato`,
	`${arenaPath}.aap115[0].fraDato`,
]

const getFeilmelding = (formikBag) => {
	let har25Feil = false
	let har67Feil = false
	for (let path of errorPaths) {
		const feil = _get(formikBag.errors, path)
		if (feil && feil.includes('25')) {
			har25Feil = true
		} else if (feil && feil.includes('67')) {
			har67Feil = true
		}
	}

	if (har25Feil) {
		if (har67Feil) {
			return 'Feilmelidng for begge'
		} else {
			return 'Feilemlding for 25'
		}
	} else if (har67Feil) {
		return 'Feilmelding for 67'
	}
	// hva med begge feil?
	return null
}

export const ArenaForm = ({ formikBag }) => {
	const servicebehovAktiv =
		_get(formikBag.values, `${arenaPath}.arenaBrukertype`) === 'MED_SERVICEBEHOV'
	const dagpengerAktiv = _get(formikBag.values, `${arenaPath}.dagpenger[0]`)

	useEffect(() => {
		servicebehovAktiv &&
			!_get(formikBag.values, `${arenaPath}.kvalifiseringsgruppe`) &&
			formikBag.setFieldValue(`${arenaPath}.kvalifiseringsgruppe`, null)

		servicebehovAktiv &&
			formikBag.setFieldValue(`${arenaPath}.automatiskInnsendingAvMeldekort`, null)
	}, [])

	const feilmelding = getFeilmelding(formikBag)

	return (
		<Vis attributt={arenaPath}>
			<Panel
				heading="Arbeidsytelser"
				hasErrors={panelError(formikBag, arenaPath)}
				iconType="arena"
				startOpen={erForsteEllerTest(formikBag.values, [arenaPath])}
			>
				{dagpengerAktiv && (
					<>
						{!formikBag.values.hasOwnProperty('inntektstub') && (
							<AlertInntektskomponentenRequired vedtak={'dagpengevedtak'} />
						)}
						<Alert variant={'info'} style={{ marginBottom: '20px' }}>
							For å kunne få gyldig dagpengevedtak må det være knyttet inntektsmelding for 12
							måneder før vedtakets fra dato. Dette kan enkelt gjøres i innteksinformasjon ved å
							benytte "Generer antall måneder" feltet.
						</Alert>
					</>
				)}
				{!servicebehovAktiv && (
					<FormikDatepicker
						name={`${arenaPath}.inaktiveringDato`}
						label="Inaktiv fra dato"
						disabled={servicebehovAktiv}
					/>
				)}
				{servicebehovAktiv && <MedServicebehov formikBag={formikBag} path={arenaPath} />}
				{!servicebehovAktiv && (
					<FormikCheckbox
						name={`${arenaPath}.automatiskInnsendingAvMeldekort`}
						label="Automatisk innsending av meldekort"
						size="small"
					/>
				)}
			</Panel>
		</Vis>
	)
}

ArenaForm.validation = {
	arenaforvalter: ifPresent('$arenaforvalter', validation),
}
