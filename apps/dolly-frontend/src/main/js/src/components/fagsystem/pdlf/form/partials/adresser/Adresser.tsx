import React from 'react'
import { Bostedsadresse } from '~/components/fagsystem/pdlf/form/partials/adresser/bostedsadresse/Bostedsadresse'

export const Adresser = ({ formikBag }) => {
	return (
		<>
			<Bostedsadresse formikBag={formikBag} />
		</>
	)
}
