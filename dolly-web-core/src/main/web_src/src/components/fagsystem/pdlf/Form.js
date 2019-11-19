import React from 'react'
import * as Yup from 'yup'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { FalskIdentitet } from './falskIdentitet/FalskIdentitet'
import { UtenlandsId } from './utenlandsId/UtenlandsId'
import { KontaktinformasjonForDoedsbo } from './kontaktinformasjonForDoedsbo/KontaktinformasjonForDoedsbo'

export const PdlfForm = ({ formikBag }) => {
	return (
		<React.Fragment>
			<Panel heading="Identifikasjon" hasErrors={panelError(formikBag)}>
				<FalskIdentitet formikBag={formikBag} />
				<UtenlandsId formikBag={formikBag} />
			</Panel>
			<Panel heading="Kontaktinformasjon for dødsbo" hasErrors={panelError(formikBag)}>
				<KontaktinformasjonForDoedsbo formikBag={formikBag} />
			</Panel>
		</React.Fragment>
	)
}

PdlfForm.initialValues = {
	pdlforvalter: {
		falskIdentitet: { rettIdentitet: { identitetType: '' } },
		utenlandskIdentifikasjonsnummer: [
			{ identifikasjonsnummer: '', kilde: '', opphoert: '', utstederland: '' }
		],
		kontaktinformasjonForDoedsbo: {
			adressat: { adressatType: '' },
			adresselinje1: '',
			adresselinje2: '',
			postnummer: '',
			poststed: '',
			landkode: '',
			skifteform: '',
			utstedtDato: '',
			gyldigFom: '',
			gyldigTom: ''
		}
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
		}),
		utenlandskIdentifikasjonsnummer: Yup.array().of(
			Yup.object({
				identifikasjonsnummer: Yup.string().required('Skriv inn identifikasjonsnummer'),
				kilde: Yup.string().required('Skriv inn kilde'),
				opphoert: Yup.string().required('Vennligst velg'),
				utstederland: Yup.string().required('Vennligst velg')
			})
		),
		kontaktinformasjonForDoedsbo: {} //TODO: LEGG INN VALIDERINGER
	})
}
