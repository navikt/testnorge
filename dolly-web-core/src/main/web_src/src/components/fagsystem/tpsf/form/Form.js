import React from 'react'
import { subYears } from 'date-fns'
import * as Yup from 'yup'
import { Personinformasjon } from './personinformasjon/Personinformasjon'
import { Adresser } from './adresser/Adresser'
import DisplayFormikState from '~/utils/DisplayFormikState'

export const initialValues = {
	tpsf: {
		foedtEtter: subYears(new Date(), 80),
		foedtFoer: new Date(),
		doedsdato: '',
		statsborgerskap: '',
		statsborgerskapRegdato: '',
		innvandretFraLand: '',
		innvandretFraLandFlyttedato: '',
		utvandretTilLand: '',
		utvandretTilLandFlyttedato: '',
		harMellomnavn: true,
		sivilstand: '',
		egenAnsattDatoFom: new Date(),
		erForsvunnet: false,
		identHistorikk: [
			{
				foedtEtter: '',
				foedtFoer: '',
				identtype: '',
				kjonn: '',
				regdato: ''
			}
		],
		boadresse: {}
	}
}

export const validation = {
	tpsf: Yup.object({
		foedtEtter: Yup.string()
			.typeError('Formatet må være DD.MM.YYYY.')
			.required(),
		foedtFoer: Yup.string().typeError('Formatet må være DD.MM.YYYY.'),
		doedsdato: Yup.string().typeError('Formatet må være DD.MM.YYYY.'),
		statsborgerskap: Yup.string().required(),
		testtest: Yup.string().required(),
		statsborgerskapRegdato: Yup.string().required(),
		innvandretFraLand: '',
		innvandretFraLandFlyttedato: '',
		utvandretTilLand: '',
		utvandretTilLandFlyttedato: ''
	})
}

export const TpsfForm = ({ formikProps }) => {
	return (
		<React.Fragment>
			<Personinformasjon formikProps={formikProps} />
			<Adresser formikProps={formikProps} />

			<DisplayFormikState {...formikProps} />
		</React.Fragment>
	)
}
