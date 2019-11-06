import React from 'react'
import { subYears } from 'date-fns'
import * as Yup from 'yup'
import DisplayFormikState from '~/utils/DisplayFormikState'
import { Krrstub } from './Krrstub'

export const initialValues = {
	krrstub: {
		epost: '',
		gyldigFra: new Date(),
		mobil: '',
		registrert: true,
		reservert: ''
	}
}

export const validation = {
	krrstub: Yup.object({
		epost: '',
		gyldigFra: Yup.string().typeError('Formatet må være DD.MM.YYYY.'),
		mobil: Yup.string().matches(/^[0-9]*$/, 'Ugyldig mobilnummer'),
		registrert: '',
		reservert: Yup.string().required()
	})
}

export const KrrstubForm = ({ formikProps }) => {
	return (
		<React.Fragment>
			<Krrstub formikProps={formikProps} />

			<DisplayFormikState {...formikProps} />
		</React.Fragment>
	)
}
