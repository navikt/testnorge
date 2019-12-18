import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import {
	Attributt,
	AttributtKategori
} from '~/components/bestillingsveileder/AttributtVelger/Attributt'

export const ArenaPanel = ({ stateModifier }) => {
	const sm = stateModifier(ArenaPanel.initialValues)

	return (
		<Panel
			heading={ArenaPanel.heading}
			startOpen
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
