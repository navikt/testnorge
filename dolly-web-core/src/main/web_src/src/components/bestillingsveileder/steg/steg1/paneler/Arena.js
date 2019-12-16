import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import {
	Attributt,
	AttributtKategori
} from '~/components/bestillingsveileder/AttributtVelger/Attributt'
import TilgjengeligeMiljoer from '~/components/tilgjengeligeMiljoer/TilgjengeligeMiljoer'
import { ArenaApi } from '~/service/Api'

export const ArenaPanel = ({ stateModifier }) => {
	const sm = stateModifier(ArenaPanel.initialValues)

	const infoTekst = (
		<React.Fragment>
			Arena-data blir ikke distribuert til alle miljøer, og et eller flere av miljøene under må
			derfor velges i siste steg.
			<br />
			<TilgjengeligeMiljoer endepunkt={ArenaApi.getTilgjengeligeMiljoer} />
		</React.Fragment>
	)

	return (
		<Panel
			heading={ArenaPanel.heading}
			informasjonstekst={infoTekst}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="arena"
		>
			<AttributtKategori>
				<Attributt attr={sm.attrs.arenaforvalter} />
			</AttributtKategori>
		</Panel>
	)
}

ArenaPanel.heading = 'Arena'

ArenaPanel.initialValues = ({ set, del, has }) => ({
	arenaforvalter: {
		label: 'Aktiver/inaktiver bruker',
		checked: has('arenaforvalter'),
		add() {
			set('arenaforvalter', {
				arenaBrukertype: 'UTEN_SERVICEBEHOV',
				inaktiveringDato: null,
				kvalifiseringsgruppe: null
			})
		},
		remove() {
			del('arenaforvalter')
		}
	}
})
