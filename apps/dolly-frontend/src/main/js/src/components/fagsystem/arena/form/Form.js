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

const path = 'arenaforvalter'

export const ArenaForm = ({ formikBag }) => {
	const servicebehovAktiv = _get(formikBag.values, `${path}.arenaBrukertype`) === 'MED_SERVICEBEHOV'
	const dagpengerAktiv = _get(formikBag.values, `${path}.dagpenger[0]`)

	const opts = useContext(BestillingsveilederContext)

	const { personFoerLeggTil, tidligereBestillinger } = opts
	const uregistrert = !(personFoerLeggTil && personFoerLeggTil.arenaforvalteren)

	useEffect(() => {
		servicebehovAktiv &&
			!_get(formikBag.values, `${path}.kvalifiseringsgruppe`) &&
			formikBag.setFieldValue(`${path}.kvalifiseringsgruppe`, null)

		servicebehovAktiv &&
			!uregistrert &&
			formikBag.setFieldValue(`${path}.automatiskInnsendingAvMeldekort`, null)
	}, [])

	return (
		<Vis attributt={path}>
			<Panel
				heading="Arbeidsytelser"
				hasErrors={panelError(formikBag, path)}
				iconType="arena"
				startOpen={erForste(formikBag.values, [path])}
			>
				{personFoerLeggTil && (
					<ArenaVisning
						data={personFoerLeggTil.arenaforvalteren}
						bestillinger={tidligereBestillinger}
						useStandard={false}
					/>
				)}
				{!opts.is.leggTil && (
					<AlertStripeInfo style={{ marginBottom: '20px' }}>
						Oppretting av nye personer i TPS skjer asynkront og blir noen ganger ikke tilgjengelig
						før etter oppretting av arbeidsytelser i Arena. Oppretting mot Arena vil da typisk
						feile. Hvis dette skjer anbefales det å opprette personen først (uten arbeidsytelser) og
						deretter bruke "LEGG TIL/ENDRE" for å legge til arbeidsytelsene.
					</AlertStripeInfo>
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
						name={`${path}.inaktiveringDato`}
						label="Inaktiv fra dato"
						disabled={servicebehovAktiv}
					/>
				)}
				{servicebehovAktiv && <MedServicebehov formikBag={formikBag} path={path} />}
				{(!servicebehovAktiv || uregistrert) && (
					<FormikCheckbox
						name={`${path}.automatiskInnsendingAvMeldekort`}
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
