import React from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikProps } from 'formik'
import Panel from '~/components/ui/panel/Panel'
import { erForste, panelError } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FalskIdentitet } from '~/components/fagsystem/pdlf/form/partials/identifikasjon/falskIdentitet/FalskIdentitet'
import { UtenlandsId } from '~/components/fagsystem/pdlf/form/partials/identifikasjon/utenlandsId/UtenlandsId'

interface IdentifikasjonValues {
	formikBag: FormikProps<{}>
}

const identifikasjonAttributter = [
	'pdldata.person.falskIdentitet',
	'pdldata.person.utenlandskIdentifikasjonsnummer',
]

export const Identifikasjon = ({ formikBag }: IdentifikasjonValues) => {
	return (
		<Vis attributt={identifikasjonAttributter}>
			<Panel
				heading="Identifikasjon"
				hasErrors={panelError(formikBag, identifikasjonAttributter)}
				iconType="identifikasjon"
				startOpen={() => erForste(formikBag.values, identifikasjonAttributter)}
			>
				<Kategori title="Falsk identitet" vis="pdldata.person.falskIdentitet">
					<FalskIdentitet formikBag={formikBag} />
				</Kategori>
				<Kategori
					title="Utenlandsk identifikasjonsnummer"
					vis="pdldata.person.utenlandskIdentifikasjonsnummer"
					flex={false}
				>
					<UtenlandsId />
				</Kategori>
			</Panel>
		</Vis>
	)
}
