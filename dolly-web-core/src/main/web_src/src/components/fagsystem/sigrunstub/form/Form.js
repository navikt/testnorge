import React from 'react'
import * as Yup from 'yup'
import _get from 'lodash/get'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { InntektsaarForm } from './partials/inntektsaarForm'

export const SigrunstubForm = ({ formikBag }) => (
	<Vis attributt="sigrunstub">
		<Panel heading="Inntekt" hasErrors={panelError(formikBag)}>
			<InntektsaarForm formikBag={formikBag} />
		</Panel>
	</Vis>
)

SigrunstubForm.validation = {
	sigrunstub: Yup.array().of(
		Yup.object({
			grunnlag: Yup.array().of(
				Yup.object({
					tekniskNavn: Yup.string().required('Velg en type inntekt.'),
					verdi: Yup.number()
						.min(0, 'Tast inn et gyldig beløp')
						.required('Oppgi beløpet')
				})
			),
			inntektsaar: Yup.number()
				.integer('Ugyldig årstall')
				.required('Tast inn et gyldig år'),
			svalbardGrunnlag: Yup.array().of(
				Yup.object({
					tekniskNavn: Yup.string().required('Velg en type inntekt.'),
					verdi: Yup.number()
						.min(0, 'Tast inn et gyldig beløp')
						.required('Oppgi beløpet')
				})
			),
			tjeneste: Yup.string().required('Velg en type tjeneste.')
		})
	)
}
