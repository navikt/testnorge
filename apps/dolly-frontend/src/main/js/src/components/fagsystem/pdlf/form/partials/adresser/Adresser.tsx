import React from 'react'
import { Bostedsadresse } from '~/components/fagsystem/pdlf/form/partials/adresser/bostedsadresse/Bostedsadresse'
import { Oppholdsadresse } from '~/components/fagsystem/pdlf/form/partials/adresser/oppholdsadresse/Oppholdsadresse'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { Kontaktadresse } from '~/components/fagsystem/pdlf/form/partials/adresser/kontaktadresse/Kontaktadresse'

export const Adresser = ({ formikBag }) => {
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
		</>
	)
}
