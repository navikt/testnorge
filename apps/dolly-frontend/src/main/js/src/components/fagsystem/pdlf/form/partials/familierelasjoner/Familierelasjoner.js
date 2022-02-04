import React, { useContext } from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { erForste, panelError } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import { Sivilstand } from '~/components/fagsystem/pdlf/form/partials/familierelasjoner/sivilstand/Sivilstand'

const relasjonerAttributter = ['pdldata.person.sivilstand']

export const Familierelasjoner = ({ formikBag }) => {
	const opts = useContext(BestillingsveilederContext)
	const { gruppeId } = opts

	return (
		<Vis attributt={relasjonerAttributter}>
			<Panel
				heading="Familierelasjoner"
				hasErrors={panelError(formikBag, relasjonerAttributter)}
				iconType={'relasjoner'}
				startOpen={() => erForste(formikBag.values, [relasjonerAttributter])}
			>
				<Kategori title="Sivilstand (partner)" vis="pdldata.person.sivilstand">
					<Sivilstand formikBag={formikBag} gruppeId={gruppeId} />
				</Kategori>
			</Panel>
		</Vis>
	)
}
