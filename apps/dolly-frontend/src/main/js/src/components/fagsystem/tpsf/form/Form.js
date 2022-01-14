import React from 'react'
import { validation } from './validation'
import { Personinformasjon } from './personinformasjon/Personinformasjon'
import { Identhistorikk } from './Identhistorikk'
import { Familierelasjoner } from './familierelasjoner/Familierelasjoner'

export const TpsfForm = ({ formikBag }) => {
	return (
		<React.Fragment>
			<Personinformasjon formikBag={formikBag} />
			<Identhistorikk formikBag={formikBag} />
			<Familierelasjoner formikBag={formikBag} />
		</React.Fragment>
	)
}

TpsfForm.validation = validation
