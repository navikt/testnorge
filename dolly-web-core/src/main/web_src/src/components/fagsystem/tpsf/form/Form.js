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
