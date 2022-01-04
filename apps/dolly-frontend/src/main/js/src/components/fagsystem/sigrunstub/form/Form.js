import React from 'react'
import * as Yup from 'yup'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { erForste, panelError } from '~/components/ui/form/formUtils'
import { InntektsaarForm } from './partials/inntektsaarForm'
import { ifPresent } from '~/utils/YupValidations'

const sigrunAttributt = 'sigrunstub'
export const SigrunstubForm = ({ formikBag }) => (
	<Vis attributt={sigrunAttributt}>
		<Panel
			heading="Skatteoppgjør (Sigrun)"
			hasErrors={panelError(formikBag, sigrunAttributt)}
			iconType="sigrun"
			startOpen={() => erForste(formikBag.values, [sigrunAttributt])}
		>
			<InntektsaarForm formikBag={formikBag} />
		</Panel>
	</Vis>
)

SigrunstubForm.validation = {
	sigrunstub: ifPresent(
		'$sigrunstub',
		Yup.array().of(
			Yup.object({
				grunnlag: Yup.array()
					.of(
						Yup.object({
							tekniskNavn: Yup.string().required('Velg en type inntekt'),
							verdi: Yup.number()
								.min(0, 'Tast inn et gyldig beløp')
								.typeError('Tast inn et gyldig beløp'),
						})
					)
					.test('is-required', 'Legg til minst én inntekt', function checkTjenesteGrunnlag(val) {
						const values = this.options.context
						const path = this.options.path
						const index = path.charAt(path.indexOf('[') + 1)
						if (values.sigrunstub[index].tjeneste === 'BEREGNET_SKATT') {
							return values.sigrunstub[index].grunnlag.length > 0
						} else if (values.sigrunstub[index].tjeneste === 'SUMMERT_SKATTEGRUNNLAG') {
							return (
								values.sigrunstub[index].grunnlag.length > 0 ||
								values.sigrunstub[index].svalbardGrunnlag.length > 0
							)
						} else return true
					}),
				inntektsaar: Yup.number().integer('Ugyldig årstall').required('Tast inn et gyldig år'),
				svalbardGrunnlag: Yup.array().of(
					Yup.object({
						tekniskNavn: Yup.string().required('Velg en type inntekt'),
						verdi: Yup.number()
							.min(0, 'Tast inn et gyldig beløp')
							.typeError('Tast inn et gyldig beløp'),
					})
				),
				tjeneste: Yup.string().required('Velg en type tjeneste'),
			})
		)
	),
}
