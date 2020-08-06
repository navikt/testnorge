import React from 'react'
import * as Yup from 'yup'
import { isAfter, isBefore, addDays } from 'date-fns'
import {
	requiredDate,
	requiredString,
	requiredNumber,
	messages,
	ifPresent
} from '~/utils/YupValidations'
import _isEmpty from 'lodash/isEmpty'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { erForste } from '~/components/ui/form/formUtils'
import { SykemeldingForm } from './partials/Sykemelding'

const sykdomAttributt = 'sykdom.sykemelding'

export const SykdomForm = ({ formikBag }) => (
	<Vis attributt={sykdomAttributt}>
		<Panel
			heading="Sykemelding"
			hasErrors={panelError(formikBag, sykdomAttributt)}
			iconType="sykdom"
			startOpen={() => erForste(formikBag.values, sykdomAttributt)}
		>
			<SykemeldingForm formikBag={formikBag} />
		</Panel>
	</Vis>
)

SykdomForm.validation = {
	sykdom: ifPresent(
		'$sykdom',
		Yup.object({
			sykemelding: ifPresent(
				'$sykdom.sykemelding',
				Yup.object({
					startDato: requiredDate,
					orgnummer: ifPresent('$sykdom.sykemelding.orgnummer', requiredString),
					arbeidsforholdId: ifPresent(
						'$sykdom.sykemelding.orgnummer',
						requiredNumber.transform(num => (isNaN(num) ? undefined : num))
					),
					hovedDiagnose: ifPresent(
						'$sykdom.sykemelding.hovedDiagnose',
						Yup.object({
							diagnose: requiredString,
							diagnosekode: requiredString,
							system: requiredString
						})
					),
					biDiagnoser: Yup.array().of(
						ifPresent(
							'$sykdom.sykemelding.biDiagnoser[0].diagnose',
							Yup.object({
								diagnose: requiredString,
								diagnosekode: requiredString,
								system: requiredString
							})
						)
					),
					// lege: ifPresent('$sykdom.sykemelding.lege',
					// 	Yup.object({
					// 		etternavn: requiredString,
					// 		fornavn: requiredString,
					// 		ident: requiredString,
					// 		hprId: requiredString
					// 	})
					// )
					arbeidsgiver: ifPresent(
						'$sykdom.sykemelding.arbeidsgiver',
						Yup.object({
							navn: requiredString,
							stillingsprosent: requiredNumber.transform(num => (isNaN(num) ? undefined : num)),
							yrkesbetegnelse: requiredString
						})
					),
					perioder: ifPresent(
						'$sykdom.sykemelding.perioder',
						Yup.array().of(
							Yup.object({
								// TODO: Skriv denne!
								aktivitet: Yup.object({}),
								// TODO: Få denne til å virke!
								fom: Yup.string()
									.test('is-not-overlapping', 'Perioder kan ikke overlappe', function validDate(
										dato
									) {
										if (!dato) return true
										const values = this.options.context
										// const path = this.options.path
										const periodeListe = values.sykdom.sykemelding.perioder
										console.log('periodeListe :>> ', periodeListe)

										let datoErGyldig = true

										if (periodeListe && periodeListe.length > 1) {
											periodeListe.map((periode, idx) => {
												if (
													// idx > 0 &&
													isAfter(new Date(dato), addDays(new Date(periode.fom), -1)) &&
													isBefore(new Date(dato), new Date(periode.tom))
												) {
													datoErGyldig = false
												}
												console.log('datoErGyldig :>> ', datoErGyldig)
											})
										}
										return datoErGyldig
									})
									.required(messages.required),
								// TODO: Fiks denne!
								tom: requiredDate
							})
						)
					)
				})
			)
		})
	)
}
