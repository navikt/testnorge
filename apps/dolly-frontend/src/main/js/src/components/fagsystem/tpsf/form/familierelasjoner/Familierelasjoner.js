import React, { useContext } from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { erForste, panelError } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import { Sivilstand } from '~/components/fagsystem/pdlf/form/partials/sivilstand/Sivilstand'
import { ForelderBarnRelasjon } from '~/components/fagsystem/pdlf/form/partials/ForelderBarnRelasjon/ForelderBarnRelasjon'

const infoTekst =
	'Savner du noen egenskaper for partner/barn? Du kan nå opprette personene hver for seg (uten relasjoner) og koble dem sammen etter de er bestilt. På denne måten kan partner og barn få flere typer egenskaper. Hvis du vil legge inn familierelasjoner raskt gjør du dette her.'

const relasjonerAttributter = [
	'tpsf.relasjoner',
	'pdldata.person.sivilstand',
	'pdldata.person.forelderBarnRelasjon',
]

export const Familierelasjoner = ({ formikBag }) => {
	const opts = useContext(BestillingsveilederContext)
	const { gruppeId } = opts

	return (
		<Vis attributt={relasjonerAttributter}>
			<Panel
				heading="Familierelasjoner"
				informasjonstekst={infoTekst}
				hasErrors={panelError(formikBag, 'pdldata.person')}
				iconType={'relasjoner'}
				startOpen={() => erForste(formikBag.values, [relasjonerAttributter])}
			>
				<Kategori title="Sivilstand (partner)" vis="pdldata.person.sivilstand">
					<Sivilstand formikBag={formikBag} gruppeId={gruppeId} />
				</Kategori>
				<Kategori title="Barn/Foreldre" vis="pdldata.person.forelderBarnRelasjon">
					<ForelderBarnRelasjon
						formikBag={formikBag}
						personFoerLeggTil={opts.personFoerLeggTil}
						gruppeId={gruppeId}
					/>
				</Kategori>
			</Panel>
		</Vis>
	)
}
