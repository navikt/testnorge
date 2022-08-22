import React from 'react'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikProps } from 'formik'

interface UkjentBostedValues {
	formikBag: FormikProps<{}>
	path: string
}

export const UkjentBosted = ({ formikBag, path }: UkjentBostedValues) => {
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
