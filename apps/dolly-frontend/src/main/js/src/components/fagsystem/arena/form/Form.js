import React, { useContext, useEffect } from 'react'
import _get from 'lodash/get'
import { ifPresent } from '~/utils/YupValidations'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { erForste, panelError } from '~/components/ui/form/formUtils'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { MedServicebehov } from './partials/MedServicebehov'
import { AlertInntektskomponentenRequired } from '~/components/ui/brukerAlert/AlertInntektskomponentenRequired'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import { validation } from '~/components/fagsystem/arena/form/validation'
import { ArenaVisning } from '~/components/fagsystem/arena/visning/ArenaVisning'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

export const arenaPath = 'arenaforvalter'

export const ArenaForm = ({ formikBag }) => {
	const servicebehovAktiv =
		_get(formikBag.values, `${arenaPath}.arenaBrukertype`) === 'MED_SERVICEBEHOV'
	const dagpengerAktiv = _get(formikBag.values, `${arenaPath}.dagpenger[0]`)

	const opts = useContext(BestillingsveilederContext)

	const { personFoerLeggTil, tidligereBestillinger } = opts
	const uregistrert = !(personFoerLeggTil && personFoerLeggTil.arenaforvalteren)

	useEffect(() => {
		servicebehovAktiv &&
			!_get(formikBag.values, `${arenaPath}.kvalifiseringsgruppe`) &&
			formikBag.setFieldValue(`${arenaPath}.kvalifiseringsgruppe`, null)

		servicebehovAktiv &&
			!uregistrert &&
			formikBag.setFieldValue(`${arenaPath}.automatiskInnsendingAvMeldekort`, null)
	}, [])

	return (
		<Vis attributt={arenaPath}>
			<Panel
				heading="Arbeidsytelser"
				hasErrors={panelError(formikBag, arenaPath)}
				iconType="arena"
				startOpen={erForste(formikBag.values, [arenaPath])}
			>
				{personFoerLeggTil && (
					<ArenaVisning
						data={personFoerLeggTil.arenaforvalteren}
						bestillinger={tidligereBestillinger}
						useStandard={false}
					/>
				)}
				{dagpengerAktiv && (
					<>
						{!(
							formikBag.values.hasOwnProperty('inntektstub') ||
							(personFoerLeggTil && personFoerLeggTil.inntektstub)
						) && <AlertInntektskomponentenRequired vedtak={'dagpengevedtak'} />}
						<AlertStripeInfo style={{ marginBottom: '20px' }}>
							For å kunne få gyldig dagpengevedtak må det være knyttet inntektsmelding for 12
							måneder før vedtakets fra dato. Dette kan enkelt gjøres i innteksinformasjon ved å
							benytte "Generer antall måneder" feltet.
						</AlertStripeInfo>
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
				{(!servicebehovAktiv || uregistrert) && (
					<FormikCheckbox
						name={`${arenaPath}.automatiskInnsendingAvMeldekort`}
						label="Automatisk innsending av meldekort"
						size="large"
					/>
				)}
			</Panel>
		</Vis>
	)
}

ArenaForm.validation = {
	arenaforvalter: ifPresent('$arenaforvalter', validation),
}
