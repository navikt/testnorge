import React from 'react'
import _isEmpty from 'lodash/isEmpty'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { validation } from './validation'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FalskIdentitet } from './partials/falskIdentitet/FalskIdentitet'
import { UtenlandsId } from './partials/utenlandsId/UtenlandsId'
import { KontaktinformasjonForDoedsbo } from './partials/kontaktinformasjonForDoedsbo/KontaktinformasjonForDoedsbo'

export const PdlfForm = ({ formikBag }) => (
	<React.Fragment>
		<Vis
			attributt={['pdlforvalter.falskIdentitet', 'pdlforvalter.utenlandskIdentifikasjonsnummer']}
		>
			<Panel heading="Identifikasjon" hasErrors={panelError(formikBag)} iconType="identifikasjon">
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

		<Vis attributt="pdlforvalter.kontaktinformasjonForDoedsbo">
			<Panel
				heading="Kontaktinformasjon for dødsbo"
				hasErrors={panelError(formikBag)}
				iconType="doedsbo"
			>
				<KontaktinformasjonForDoedsbo formikBag={formikBag} />
			</Panel>
		</Vis>
	</React.Fragment>
)

PdlfForm.validation = validation
