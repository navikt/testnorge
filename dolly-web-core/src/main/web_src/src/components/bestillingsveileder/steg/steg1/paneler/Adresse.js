import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import {
	Attributt,
	AttributtKategori
} from '~/components/bestillingsveileder/AttributtVelger/Attributt'

export const AdressePanel = ({ stateModifier }) => {
	const sm = stateModifier(AdressePanel.initialValues)

	return (
		<Panel
			heading={AdressePanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="adresse"
		>
			<AttributtKategori title="Boadresse">
				<Attributt attr={sm.attrs.boadresse} />
			</AttributtKategori>

			<AttributtKategori title="Postadresse">
				<Attributt attr={sm.attrs.postadresse} />
			</AttributtKategori>
		</Panel>
	)
}

AdressePanel.heading = 'Adresser'

AdressePanel.initialValues = ({ set, setMulti, del, has }) => ({
	boadresse: {
		label: 'Har boadresse',
		checked: has('tpsf.boadresse'),
		add() {
			setMulti(['tpsf.boadresse', { flyttedato: null }], ['tpsf.adresseNrInfo', null])
		},
		remove() {
			del(['tpsf.boadresse', 'tpsf.adresseNrInfo'])
		}
	},
	postadresse: {
		label: 'Har postadresse',
		checked: has('tpsf.postadresse'),
		add: () => set('tpsf.postadresse', {}),
		remove: () => del('tpsf.postadresse')
	}
})
