import React from 'react'
import _isEmpty from 'lodash/isEmpty'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { validation } from './validation'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { erForste } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FalskIdentitet } from './partials/falskIdentitet/FalskIdentitet'
import { UtenlandsId } from './partials/utenlandsId/UtenlandsId'
import { KontaktinformasjonForDoedsbo } from './partials/kontaktinformasjonForDoedsbo/KontaktinformasjonForDoedsbo'

const identifikasjonAttributt = [
	'pdlforvalter.falskIdentitet',
	'pdlforvalter.utenlandskIdentifikasjonsnummer'
]
const doedsboAttributt = 'pdlforvalter.kontaktinformasjonForDoedsbo'

export const PdlfForm = ({ formikBag }) => (
	<React.Fragment>
		<Vis attributt={identifikasjonAttributt}>
			<Panel
				heading="Identifikasjon"
				hasErrors={panelError(formikBag, identifikasjonAttributt)}
				iconType="identifikasjon"
				startOpen={() => erForste(formikBag.values, identifikasjonAttributt)}
			>
				<Kategori title="Falsk identitet" vis="pdlforvalter.falskIdentitet">
					<FalskIdentitet formikBag={formikBag} />
				</Kategori>
				<Kategori
					title="Utenlandsk identifikasjonsnummer"
					vis="pdlforvalter.utenlandskIdentifikasjonsnummer"
					flex={false}
				>
					<UtenlandsId formikBag={formikBag} />
				</Kategori>
			</Panel>
		</Vis>

		<Vis attributt={doedsboAttributt}>
			<Panel
				heading="Kontaktinformasjon for dødsbo"
				hasErrors={panelError(formikBag, doedsboAttributt)}
				iconType="doedsbo"
				startOpen={() => erForste(formikBag.values, [doedsboAttributt])}
			>
				<KontaktinformasjonForDoedsbo formikBag={formikBag} />
			</Panel>
		</Vis>
	</React.Fragment>
)

PdlfForm.validation = validation
