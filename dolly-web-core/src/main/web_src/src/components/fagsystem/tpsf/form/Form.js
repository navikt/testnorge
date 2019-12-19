import React from 'react'
import _isEmpty from 'lodash/isEmpty'
import * as Yup from 'yup'
import { Personinformasjon } from './personinformasjon/Personinformasjon'
import { Adresser } from './adresser/Adresser'
import { Identhistorikk } from './Identhistorikk'
import { requiredString, ifPresent, ifKeyHasValue, messages } from '~/utils/YupValidations'

export const TpsfForm = ({ formikBag }) => {
	return (
		<React.Fragment>
			<Personinformasjon formikBag={formikBag} />
			<Adresser formikBag={formikBag} />
			<Identhistorikk formikBag={formikBag} />
		</React.Fragment>
	)
}

TpsfForm.validation = {
	tpsf: ifPresent(
		'$tpsf',
		Yup.object({
			alder: Yup.number()
				.min(1)
				.max(99)
				.typeError('Feltet er påkrevd'),
			foedtEtter: Yup.date().nullable(),
			foedtFoer: Yup.date().nullable(),
			doedsdato: Yup.date().nullable(),
			statsborgerskap: Yup.string(),
			statsborgerskapRegdato: Yup.date().nullable(),
			innvandretFraLand: Yup.string(),
			innvandretFraLandFlyttedato: Yup.date().nullable(),
			utvandretTilLand: Yup.string(),
			utvandretTilLandFlyttedato: Yup.date().nullable(),
			spesreg: Yup.string().when('utenFastBopel', {
				is: true,
				then: Yup.string().test(
					'is-not-kode6',
					'Kan ikke være "Kode 6" når "Uten fast bopel" er valgt.',
					value => value !== 'SPSF'
				)
			}),
			boadresse: Yup.object({
				gateadresse: ifKeyHasValue(
					'$tpsf.boadresse.adressetype',
					['GATE'],
					ifKeyHasValue(
						'$tpsf.adresseNrInfo',
						[null],
						Yup.string().required(
							'Bruk adressevelgeren over for å hente gyldige adresser og velge et av forslagene'
						)
					)
				),
				gardsnr: Yup.string().when('adressetype', {
					is: 'MATR',
					then: Yup.string()
						.min(1, 'Gårdsnummeret må være mellom 1 og 99999')
						.max(99999, 'Gårdsnummeret må være mellom 1 og 99999')
						.required(messages.required)
				}),
				bruksnr: Yup.string().when('adressetype', {
					is: 'MATR',
					then: Yup.string()
						.min(1, 'Bruksnummeret må være mellom 1 og 9999')
						.max(9999, 'Bruksnummeret må være mellom 1 og 9999')
						.required(messages.required)
				}),
				festnr: Yup.string()
					.min(1, 'Festenummer må være mellom 1 og 9999')
					.max(9999, 'Festenummer må være mellom 1 og 9999'),
				undernr: Yup.string()
					.min(1, 'Undernummer må være mellom 1 og 999')
					.max(999, 'Undernummer må være mellom 1 og 999'),
				postnr: Yup.string().when('adressetype', { is: 'MATR', then: requiredString }),
				kommunenr: Yup.string().when('adressetype', { is: 'MATR', then: requiredString })
			}),
			adresseNrInfo: Yup.object({
				nummer: Yup.string().when('nummertype', {
					is: v => v,
					then: requiredString
				})
			}).nullable()
		})
	)
}
