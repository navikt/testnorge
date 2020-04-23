import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { boadressePaths } from '~/components/fagsystem/tpsf/form/adresser/Adresser'
import { Attributt, AttributtKategori } from '../Attributt'

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
		checked: boadressePaths.some(path => has(path)),
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
		add: () =>
			set('tpsf.postadresse', [
				{
					postLand: 'NOR',
					postLinje1: '',
					postLinje2: '',
					postLinje3: ''
				}
			]),
		remove: () => del('tpsf.postadresse')
	}
})
