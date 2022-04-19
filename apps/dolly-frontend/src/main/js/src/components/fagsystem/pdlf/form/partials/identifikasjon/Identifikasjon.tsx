import React from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikProps } from 'formik'
import Panel from '~/components/ui/panel/Panel'
import { erForste, panelError } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FalskIdentitet } from '~/components/fagsystem/pdlf/form/partials/identifikasjon/falskIdentitet/FalskIdentitet'
import { UtenlandsId } from '~/components/fagsystem/pdlf/form/partials/identifikasjon/utenlandsId/UtenlandsId'
import { NyIdent } from '~/components/fagsystem/pdlf/form/partials/nyIdent/nyIdent'

interface IdentifikasjonValues {
	formikBag: FormikProps<{}>
}

const identifikasjonAttributter = [
	'pdldata.person.falskIdentitet',
	'pdldata.person.utenlandskIdentifikasjonsnummer',
	'pdldata.person.nyident',
]

const hjelpetekst =
	'Her kan du velge ny identitet for person, enten fra en eksisterende ident, eller ved å opprette en helt ny ident. Ny ident vil settes som gjeldende, og tidligere valgte attributter vil settes som identhistorikk på personen.'

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
				<Kategori title="Ny identitet" vis="pdldata.person.nyident" hjelpetekst={hjelpetekst}>
					<NyIdent formikBag={formikBag} />
				</Kategori>
			</Panel>
		</Vis>
	)
}
