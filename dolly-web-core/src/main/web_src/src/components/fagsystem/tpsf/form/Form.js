import React from 'react'
import { subYears } from 'date-fns'
import * as Yup from 'yup'
import { Personinformasjon } from './personinformasjon/Personinformasjon'
import { Adresser } from './adresser/Adresser'

export const TpsfForm = ({ formikBag }) => {
	return (
		<React.Fragment>
			<Personinformasjon formikBag={formikBag} />
			<Adresser formikBag={formikBag} />
		</React.Fragment>
	)
}

TpsfForm.initialValues = {
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

TpsfForm.validation = {
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
