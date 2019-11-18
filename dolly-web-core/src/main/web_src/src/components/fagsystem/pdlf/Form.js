import React from 'react'
import * as Yup from 'yup'
import { Identifikasjon } from './identifikasjon/Identifikasjon'

export const PdlfForm = ({ formikBag }) => {
	return (
		<React.Fragment>
			<Identifikasjon formikBag={formikBag} />
		</React.Fragment>
	)
}

PdlfForm.initialValues = {
	pdlforvalter: {
		falskIdentitet: { rettIdentitet: { identitetType: '' } }
	}
}

PdlfForm.validation = {
	pdlforvalter: Yup.object({
		falskIdentitet: Yup.object({
			rettIdentitet: Yup.object({
				identitetType: Yup.string().required('Vennligst velg'),
				rettIdentitetVedIdentifikasjonsnummer: Yup.string().required(
					'Skriv inn identifikasjonsnummer'
				),
				personnavn: Yup.object({
					fornavn: Yup.string().required('Skriv inn fornavn'),
					mellomnavn: Yup.string(),
					etternavn: Yup.string().required('Skriv inn etternavn')
				}),
				foedselsdato: Yup.string()
					.typeError('Formatet må være DD.MM.YYYY.')
					.required(),
				kjoenn: Yup.string().required('Vennligst velg'),
				statsborgerskap: Yup.string().required('Vennligst velg')
			})
		})
	})
}
