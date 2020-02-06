import React from 'react'
import _get from 'lodash/get'
import Panel from '~/components/ui/panel/Panel'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { panelError } from '~/components/ui/form/formUtils'
import { erForste } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { Partnere } from './partials/Partnere'
import { Barn } from './partials/Barn'

const infoTekst =
	'Savner du noen egenskaper for partner/barn? Du kan nå opprette personene hver for seg (uten relasjoner) og koble dem sammen etter de er bestilt. På denne måten kan partner og barn få flere typer egenskaper. Hvis du vil legge inn familierelasjoner raskt gjør du dette her.'

const relasjonerAttributt = 'tpsf.relasjoner'

export const Familierelasjoner = ({ formikBag }) => (
	<Vis attributt={relasjonerAttributt}>
		<Panel
			heading="Familierelasjoner"
			informasjonstekst={infoTekst}
			hasErrors={panelError(formikBag, 'tpsf.relasjoner')}
			iconType={'relasjoner'}
			startOpen={() => erForste(formikBag.values, [relasjonerAttributt])}
		>
			{/* Fiks under fjernes når barn: null og partere: null blir fikset backend. */}
			{/* De vil være huket av i steg 1 selv om de ikke har noe info (null), så det må fikses */}
			{_get(formikBag, 'values.tpsf.relasjoner.partnere') && (
				<Kategori title="Partnere" vis="tpsf.relasjoner.partnere">
					<Partnere formikBag={formikBag} />
				</Kategori>
			)}
			{_get(formikBag, 'values.tpsf.relasjoner.barn') && (
				<Kategori title="Barn" vis="tpsf.relasjoner.barn">
					<Barn formikBag={formikBag} />
				</Kategori>
			)}
		</Panel>
	</Vis>
)
