import React from 'react'
import * as Yup from 'yup'
import _isEmpty from 'lodash/isEmpty'
import { Vis, pathAttrs } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { requiredString, ifKeyHasValue } from '~/utils/YupValidations'
import { FalskIdentitet } from './falskIdentitet/FalskIdentitet'
import { UtenlandsId } from './utenlandsId/UtenlandsId'
import { KontaktinformasjonForDoedsbo } from './kontaktinformasjonForDoedsbo/KontaktinformasjonForDoedsbo'

export const PdlfForm = ({ formikBag, props }) => {
	return (
		<React.Fragment>
			<Vis attributt={pathAttrs.kategori.identifikasjon}>
				<Panel heading="Identifikasjon" hasErrors={panelError(formikBag)}>
					<Kategori title="Falsk identitet" vis="pdlforvalter.falskIdentitet">
						<FalskIdentitet formikBag={formikBag} />
					</Kategori>
					<Kategori
						title="Utenlandsk identifikasjonsnummer"
						vis="pdlforvalter.utenlandskIdentifikasjonsnummer"
					>
						<UtenlandsId formikBag={formikBag} />
					</Kategori>
				</Panel>
			</Vis>
			<Vis attributt={pathAttrs.kategori.kontaktinformasjonForDoedsbo}>
				<Panel heading="Kontaktinformasjon for dødsbo" hasErrors={panelError(formikBag)}>
					<KontaktinformasjonForDoedsbo formikBag={formikBag} props={props} />
				</Panel>
			</Vis>
		</React.Fragment>
	)
}

PdlfForm.initialValues = attrs => {
	const initial = {
		pdlforvalter: {}
	}

	if (attrs.falskIdentitet)
		initial.pdlforvalter.falskIdentitet = { rettIdentitet: { identitetType: 'UKJENT' } }

	if (attrs.utenlandskIdentifikasjonsnummer)
		initial.pdlforvalter.utenlandskIdentifikasjonsnummer = [
			{ identifikasjonsnummer: '', kilde: '', opphoert: '', utstederland: '' }
		]

	if (attrs.kontaktinformasjonForDoedsbo)
		initial.pdlforvalter.kontaktinformasjonForDoedsbo = {
			adressat: { adressatType: '' },
			adresselinje1: '',
			adresselinje2: '',
			postnummer: '',
			poststed: '',
			landkode: 'NOR',
			skifteform: '',
			utstedtDato: ''
		}

	return _isEmpty(initial.pdlforvalter) ? null : initial
}

const personnavnSchema = Yup.object({
	fornavn: requiredString,
	etternavn: requiredString
})

PdlfForm.validation = {
	pdlforvalter: Yup.object({
		falskIdentitet: Yup.object({
			rettIdentitet: Yup.object({
				identitetType: Yup.string().required('Vennligst velg'),
				rettIdentitetVedIdentifikasjonsnummer: Yup.string().when('identitetType', {
					is: 'ENTYDIG',
					then: Yup.string().required('Skriv inn identifikasjonsnummer')
				}),
				personnavn: ifKeyHasValue(
					'$pdlforvalter.falskIdentitet.rettIdentitet.identitetType',
					['OMTRENTLIG'],
					personnavnSchema
				),
				foedselsdato: Yup.string().when('identitetType', {
					is: 'OMTRENTLIG',
					then: Yup.string()
						.typeError('Formatet må være DD.MM.YYYY.')
						.required('Skriv inn fødselsdato')
				}),
				kjoenn: Yup.string().when('identitetType', {
					is: 'OMTRENTLIG',
					then: Yup.string().required('Vennligst velg')
				}),
				statsborgerskap: Yup.string().when('identitetType', {
					is: 'OMTRENTLIG',
					then: Yup.string().required('Vennligst velg')
				})
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
		kontaktinformasjonForDoedsbo: Yup.object({
			adressat: Yup.object({
				adressatType: Yup.string().required('Velg adressattype'),
				kontaktperson: ifKeyHasValue(
					'$pdlforvalter.kontaktinformasjonForDoedsbo.adressat.adressatType',
					['ADVOKAT', 'ORGANISASJON'],
					personnavnSchema
				),
				organisasjonsnavn: Yup.string().when('adressatType', {
					is: 'ORGANISASJON',
					then: Yup.string().required('Skriv inn organisasjonsnavn')
				}),
				organisasjonsnummer: Yup.string().when('adressatType', {
					is: 'ADVOKAT' || 'ORGANISASJON',
					then: Yup.string().matches(/^[0-9]{9}$/, {
						message: 'Organisasjonsnummeret må være et tall med 9 sifre',
						excludeEmptyString: true
					})
				}),
				idnummer: Yup.string().when('adressatType', {
					is: 'PERSON_MEDID',
					then: Yup.string()
						.required('Skriv inn id-nummer')
						.matches(/^[0-9]{11}$/, {
							message: 'Id-nummeret må være et tall med 11 sifre',
							excludeEmptyString: true
						})
				}),
				navn: ifKeyHasValue(
					'$pdlforvalter.kontaktinformasjonForDoedsbo.adressat.adressatType',
					['PERSON_UTENID'],
					personnavnSchema
				)
			}),
			adresselinje1: Yup.string().required('Skriv inn adresse'),
			adresselinje2: Yup.string(),
			postnummer: Yup.string().required('Skriv inn postnummer'),
			poststed: Yup.string().required('Skriv inn poststed'),
			landkode: Yup.string().required('Velg land'),
			skifteform: Yup.string().required('Velg skifteform'),
			utstedtDato: Yup.string()
				.typeError('Formatet må være DD.MM.YYYY.')
				.required()
		})
	})
}
