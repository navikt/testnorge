import React, { useContext } from 'react'
import _get from 'lodash/get'
import _has from 'lodash/has'
import Panel from '~/components/ui/panel/Panel'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { panelError } from '~/components/ui/form/formUtils'
import { erForste } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { Partnere } from './partials/partnere/Partnere'
import LeggTilPaaPartnere from './partials/partnere/leggTil/LeggTilPaaPartnere'
import { Barn } from './partials/Barn'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'

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
					{_has(opts, 'personFoerLeggTil') ? (
						<LeggTilPaaPartnere formikBag={formikBag} personFoerLeggTil={opts.personFoerLeggTil} />
					) : (
						<Partnere formikBag={formikBag} />
					)}
				</Kategori>
				<Kategori title="Barn" vis="tpsf.relasjoner.barn">
					<Barn formikBag={formikBag} />
				</Kategori>
			</Panel>
		</Vis>
	)
}
