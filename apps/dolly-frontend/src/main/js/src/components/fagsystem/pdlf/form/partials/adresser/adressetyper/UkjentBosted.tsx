import React from 'react'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'

export const UkjentBosted = ({ formikBag, path }) => {
	console.log('path', path)
	return (
		<div className="flexbox--flex-wrap">
			<FormikSelect
				name={`${path}.bostedskommune`}
				label="Bostedskommune"
				kodeverk={AdresseKodeverk.Kommunenummer}
				size="large"
			/>
		</div>
	)
}
