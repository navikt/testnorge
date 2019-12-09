import React from 'react'
import _isEmpty from 'lodash/isEmpty'
import * as Yup from 'yup'
import { validation } from './validation'
import { Personinformasjon } from './personinformasjon/Personinformasjon'
import { Adresser } from './adresser/Adresser'
import { requiredDate, requiredString, ifPresent } from '~/utils/YupValidations'

export const TpsfForm = ({ formikBag }) => {
	return (
		<React.Fragment>
			<Personinformasjon formikBag={formikBag} />
			<Adresser formikBag={formikBag} />
		</React.Fragment>
	)
}

TpsfForm.validation = validation
