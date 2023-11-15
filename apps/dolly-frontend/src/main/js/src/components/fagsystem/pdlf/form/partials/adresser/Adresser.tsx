import { Bostedsadresse } from '@/components/fagsystem/pdlf/form/partials/adresser/bostedsadresse/Bostedsadresse'
import { Oppholdsadresse } from '@/components/fagsystem/pdlf/form/partials/adresser/oppholdsadresse/Oppholdsadresse'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { Kontaktadresse } from '@/components/fagsystem/pdlf/form/partials/adresser/kontaktadresse/Kontaktadresse'
import { Adressebeskyttelse } from '@/components/fagsystem/pdlf/form/partials/adresser/adressebeskyttelse/Adressebeskyttelse'
import { FormikProps } from 'formik'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'

interface AdresserValues {
	formikBag: FormikProps<{}>
}

export const adresseAttributter = [
	'pdldata.person.bostedsadresse',
	'pdldata.person.oppholdsadresse',
	'pdldata.person.kontaktadresse',
	'pdldata.person.adressebeskyttelse',
]

export const Adresser = ({ formMethods }: AdresserValues) => {
	return (
		<Vis attributt={adresseAttributter}>
			<Panel
				heading="Adresser"
				hasErrors={panelError(formikBag, adresseAttributter)}
				iconType="adresse"
				startOpen={erForsteEllerTest(formikBag.values, adresseAttributter)}
			>
				<Vis attributt={'pdldata.person.bostedsadresse'}>
					<Bostedsadresse formMethods={formMethods} />
				</Vis>
				<Vis attributt={'pdldata.person.oppholdsadresse'}>
					<Oppholdsadresse formMethods={formMethods} />
				</Vis>
				<Vis attributt={'pdldata.person.kontaktadresse'}>
					<Kontaktadresse formMethods={formMethods} />
				</Vis>
				<Vis attributt={'pdldata.person.adressebeskyttelse'}>
					<Adressebeskyttelse formMethods={formMethods} />
				</Vis>
			</Panel>
		</Vis>
	)
}
