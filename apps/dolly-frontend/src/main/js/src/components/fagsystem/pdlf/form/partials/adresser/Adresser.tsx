import React from 'react'
import { Bostedsadresse } from '~/components/fagsystem/pdlf/form/partials/adresser/bostedsadresse/Bostedsadresse'
import { Oppholdsadresse } from '~/components/fagsystem/pdlf/form/partials/adresser/oppholdsadresse/Oppholdsadresse'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { Kontaktadresse } from '~/components/fagsystem/pdlf/form/partials/adresser/kontaktadresse/Kontaktadresse'
import { Adressebeskyttelse } from '~/components/fagsystem/pdlf/form/partials/adresser/adressebeskyttelse/Adressebeskyttelse'
import { FormikProps } from 'formik'

interface AdresserValues {
	formikBag: FormikProps<{}>
}

export const Adresser = ({ formikBag }: AdresserValues) => {
	return (
		<>
			<Vis attributt={'pdldata.person.bostedsadresse'}>
				<Bostedsadresse formikBag={formikBag} />
			</Vis>
			<Vis attributt={'pdldata.person.oppholdsadresse'}>
				<Oppholdsadresse formikBag={formikBag} />
			</Vis>
			<Vis attributt={'pdldata.person.kontaktadresse'}>
				<Kontaktadresse formikBag={formikBag} />
			</Vis>
			<Vis attributt={'pdldata.person.adressebeskyttelse'}>
				<Adressebeskyttelse formikBag={formikBag} />
			</Vis>
		</>
	)
}
