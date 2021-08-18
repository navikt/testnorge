import React, { useContext } from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { erForste, panelError } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { Partnere } from './partials/partnere/Partnere'
import { Barn } from './partials/Barn'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import { Foreldre } from './partials/Foreldre'

const infoTekst =
	'Savner du noen egenskaper for partner/barn? Du kan nå opprette personene hver for seg (uten relasjoner) og koble dem sammen etter de er bestilt. På denne måten kan partner og barn få flere typer egenskaper. Hvis du vil legge inn familierelasjoner raskt gjør du dette her.'

const relasjonerAttributt = 'tpsf.relasjoner'

export const Familierelasjoner = ({ formikBag }) => {
	const opts = useContext(BestillingsveilederContext)

	return (
		<Vis attributt={relasjonerAttributt}>
			<Panel
				heading="Familierelasjoner"
				informasjonstekst={infoTekst}
				hasErrors={panelError(formikBag, 'tpsf.relasjoner')}
				iconType={'relasjoner'}
				startOpen={() => erForste(formikBag.values, [relasjonerAttributt])}
			>
				<Kategori title="Partnere" vis="tpsf.relasjoner.partnere">
					<Partnere formikBag={formikBag} personFoerLeggTil={opts.personFoerLeggTil} />
				</Kategori>
				<Kategori title="Barn" vis="tpsf.relasjoner.barn">
					<Barn formikBag={formikBag} personFoerLeggTil={opts.personFoerLeggTil} />
				</Kategori>
				<Kategori title="Foreldre" vis="tpsf.relasjoner.foreldre">
					<Foreldre formikBag={formikBag} personFoerLeggTil={opts.personFoerLeggTil} />
				</Kategori>
			</Panel>
		</Vis>
	)
}
