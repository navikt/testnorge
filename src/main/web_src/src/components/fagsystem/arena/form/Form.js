import React, { useEffect } from 'react'
import * as Yup from 'yup'
import _get from 'lodash/get'
import { isAfter, isBefore } from 'date-fns'
import { ifPresent, messages, requiredDate, requiredString } from '~/utils/YupValidations'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { erForste, panelError } from '~/components/ui/form/formUtils'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { MedServicebehov } from './partials/MedServicebehov'
import { AlertInntektskomponentenRequired } from '~/components/ui/brukerAlert/AlertInntektskomponentenRequired'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'

const arenaAttributt = 'arenaforvalter'

export const ArenaForm = ({ formikBag }) => {
	const servicebehovAktiv =
		_get(formikBag, 'values.arenaforvalter.arenaBrukertype') === 'MED_SERVICEBEHOV'
	const dagpengerAktiv = _get(formikBag, 'values.arenaforvalter.dagpenger[0]')

	useEffect(() => {
		servicebehovAktiv &&
			!_get(formikBag, 'values.arenaforvalter.kvalifiseringsgruppe') &&
			formikBag.setFieldValue('arenaforvalter.kvalifiseringsgruppe', null)
	}, [])

	return (
		<Vis attributt={arenaAttributt}>
			<Panel
				heading="Arbeidsytelser"
				hasErrors={panelError(formikBag, arenaAttributt)}
				iconType="arena"
				startOpen={() => erForste(formikBag.values, [arenaAttributt])}
			>
				{dagpengerAktiv && (
					<>
						{!formikBag.values.hasOwnProperty('inntektstub') && (
							<AlertInntektskomponentenRequired vedtak={'dagpengevedtak'} />
						)}
						<AlertStripeInfo style={{ marginBottom: '20px' }}>
							For å kunne få gyldig dagpengevedtak må det være knyttet inntektsmelding for 12
							måneder før vedtakets fra dato. Dette kan enkelt gjøres i innteksinformasjon ved å
							benytte "Generer antall måneder" feltet.
						</AlertStripeInfo>
					</>
				)}
				{!servicebehovAktiv && (
					<FormikDatepicker
						name="arenaforvalter.inaktiveringDato"
						label="Inaktiv fra dato"
						disabled={servicebehovAktiv}
					/>
				)}
				{servicebehovAktiv && <MedServicebehov formikBag={formikBag} />}
			</Panel>
		</Vis>
	)
}

const datoIkkeMellom = (nyDatoFra, gjeldendeDatoFra, gjeldendeDatoTil) => {
	if (!gjeldendeDatoFra || !gjeldendeDatoTil) return true
	return (
		isAfter(new Date(nyDatoFra), new Date(gjeldendeDatoTil)) ||
		isBefore(new Date(nyDatoFra), new Date(gjeldendeDatoFra))
	)
}

function tilDatoValidation(erDagpenger) {
	return Yup.string()
		.test('etter-fradato', 'Til-dato må være etter fra-dato', function validDate(tildato) {
			const values = this.options.context
			const fradato = erDagpenger
				? values.arenaforvalter.dagpenger[0].fraDato
				: values.arenaforvalter.aap[0].fraDato
			if (!fradato || !tildato) return true
			return isAfter(new Date(tildato), new Date(fradato))
		})
		.nullable()
		.required('Feltet er påkrevd')
}

function harGjeldendeVedtakValidation(vedtakType) {
	return Yup.string()
		.test(
			'har-gjeldende-vedtak',
			'AAP- og Dagpenger-vedtak kan ikke overlappe hverandre',
			function validVedtak() {
				const values = this.options.context
				const dagpengerFra = values.arenaforvalter.dagpenger?.[0].fraDato
				const dagpengerTil = values.arenaforvalter.dagpenger?.[0].tilDato

				const aapFra = values.arenaforvalter.aap?.[0].fraDato
				const aapTil = values.arenaforvalter.aap?.[0].tilDato

				// Hvis det bare er en type vedtak trengs det ikke å sjekkes videre
				if (!dagpengerFra && !aapFra) return true
				if (vedtakType === 'aap') {
					return datoIkkeMellom(aapFra, dagpengerFra, dagpengerTil)
				} else if (vedtakType === 'dagpenger') {
					return datoIkkeMellom(dagpengerFra, aapFra, aapTil)
				}
			}
		)
		.nullable()
		.required('Feltet er påkrevd')
}

const validation = Yup.object({
	aap: Yup.array().of(
		Yup.object({
			fraDato: harGjeldendeVedtakValidation('aap'),
			tilDato: tilDatoValidation(false)
		})
	),
	aap115: Yup.array().of(
		Yup.object({
			fraDato: requiredDate
		})
	),
	arenaBrukertype: requiredString,
	inaktiveringDato: Yup.mixed()
		.nullable()
		.when('arenaBrukertype', {
			is: 'UTEN_SERVICEBEHOV',
			then: requiredDate
		}),
	kvalifiseringsgruppe: Yup.string()
		.nullable()
		.when('arenaBrukertype', {
			is: 'MED_SERVICEBEHOV',
			then: requiredString
		}),
	dagpenger: Yup.array().of(
		Yup.object({
			rettighetKode: Yup.string().required(messages.required),
			fraDato: harGjeldendeVedtakValidation('dagpenger'),
			tilDato: tilDatoValidation(true),
			mottattDato: Yup.date().nullable()
		})
	)
})

ArenaForm.validation = {
	arenaforvalter: ifPresent('$arenaforvalter', validation)
}
