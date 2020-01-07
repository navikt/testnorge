import React from 'react'
import _isEmpty from 'lodash/isEmpty'
import { validation } from './validation'
import { Personinformasjon } from './personinformasjon/Personinformasjon'
import { Adresser } from './adresser/Adresser'
import { Identhistorikk } from './Identhistorikk'
import { Familierelasjoner } from './familierelasjoner/Familierelasjoner'
// import { requiredDate, requiredString, ifPresent } from '~/utils/YupValidations'
// import { requiredString, ifPresent, ifKeyHasValue, messages } from '~/utils/YupValidations'

export const TpsfForm = ({ formikBag }) => {
	return (
		<React.Fragment>
			<Personinformasjon formikBag={formikBag} />
			<Adresser formikBag={formikBag} />
			<Identhistorikk formikBag={formikBag} />
			<Familierelasjoner formikBag={formikBag} />
		</React.Fragment>
	)
}

TpsfForm.validation = validation
// TpsfForm.validation = {
// 	tpsf: ifPresent(
// 		'$tpsf',
// 		Yup.object({
// 			alder: Yup.number()
// 				.min(1)
// 				.max(99)
// 				.typeError('Feltet er påkrevd'),
// 			foedtEtter: Yup.date().nullable(),
// 			foedtFoer: Yup.date().nullable(),
// 			doedsdato: Yup.date().nullable(),
// 			statsborgerskap: Yup.string(),
// 			statsborgerskapRegdato: Yup.date().nullable(),
// 			innvandretFraLand: Yup.string(),
// 			innvandretFraLandFlyttedato: Yup.date().nullable(),
// 			utvandretTilLand: Yup.string(),
// 			utvandretTilLandFlyttedato: Yup.date().nullable(),
// 			spesreg: Yup.string().when('utenFastBopel', {
// 				is: true,
// 				then: Yup.string().test(
// 					'is-not-kode6',
// 					'Kan ikke være "Kode 6" når "Uten fast bopel" er valgt.',
// 					value => value !== 'SPSF'
// 				)
// 			}),
// 			boadresse: Yup.object({
// 				gateadresse: ifKeyHasValue(
// 					'$tpsf.boadresse.adressetype',
// 					['GATE'],
// 					ifKeyHasValue(
// 						'$tpsf.adresseNrInfo',
// 						[null],
// 						Yup.string().required(
// 							'Bruk adressevelgeren over for å hente gyldige adresser og velge et av forslagene'
// 						)
// 					)
// 				),
// 				gardsnr: Yup.string().when('adressetype', {
// 					is: 'MATR',
// 					then: Yup.string()
// 						.required(messages.required)
// 						.max(5, 'Gårdsnummeret må være under 99999')
// 				}),
// 				bruksnr: Yup.string().when('adressetype', {
// 					is: 'MATR',
// 					then: Yup.string()
// 						.required(messages.required)
// 						.max(4, 'Bruksnummeret må være under 9999')
// 				}),
// 				festnr: Yup.string().max(4, 'Festenummer må være under 9999'),
// 				undernr: Yup.string().max(3, 'Undernummer må være under 999'),
// 				postnr: Yup.string().when('adressetype', { is: 'MATR', then: requiredString }),
// 				kommunenr: Yup.string().when('adressetype', { is: 'MATR', then: requiredString })
// 			}),
// 			adresseNrInfo: Yup.object({
// 				nummer: Yup.string().when('nummertype', {
// 					is: v => v,
// 					then: requiredString
// 				})
// 			}).nullable()
// 		})
// 	)
// }
