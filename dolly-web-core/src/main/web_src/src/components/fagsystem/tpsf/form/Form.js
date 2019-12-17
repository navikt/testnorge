import React from 'react'
import _isEmpty from 'lodash/isEmpty'
import { validation } from './validation'
import { Personinformasjon } from './personinformasjon/Personinformasjon'
import { Adresser } from './adresser/Adresser'
import { Identhistorikk } from './Identhistorikk'
import { Familierelasjoner } from './familierelasjoner/Familierelasjoner'
import { requiredDate, requiredString, ifPresent } from '~/utils/YupValidations'

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
// 			})
// 			// TODO familievalidation
// 		})
// 	)
// }
