import React from 'react'
import * as Yup from 'yup'
import _get from 'lodash/get'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { InntektsaarForm } from './partials/inntektsaarForm'

const initialValuesInntektsaar = {
	inntektsaar: new Date().getFullYear(),
	tjeneste: '',
	grunnlag: [],
	svalbardGrunnlag: []
}

export const SigrunstubForm = ({ formikBag }) => (
	<Panel heading="Inntekt" hasErrors={panelError(formikBag)} startOpen>
		<InntektsaarForm formikBag={formikBag} initial={initialValuesInntektsaar} />
	</Panel>
)

SigrunstubForm.initialValues = {
	sigrunstub: [initialValuesInntektsaar]
}

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
