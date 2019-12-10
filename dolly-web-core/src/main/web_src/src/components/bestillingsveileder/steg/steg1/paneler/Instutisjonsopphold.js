import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import {
	Attributt,
	AttributtKategori
} from '~/components/bestillingsveileder/AttributtVelger/Attributt'
import TilgjengeligeMiljoer from '~/components/tilgjengeligeMiljoer/TilgjengeligeMiljoer'
import { InstApi } from '~/service/Api'

export const InstutisjonsoppholdPanel = ({ stateModifier }) => {
	const sm = stateModifier(InstutisjonsoppholdPanel.initialValues)

	const infoTekst = (
		<React.Fragment>
			Data om institusjonsopphold blir ikke distribuert til alle miljøer, og et eller flere av
			miljøene under må derfor velges i siste steg.
			<br />
			<TilgjengeligeMiljoer endepunkt={InstApi.getTilgjengeligeMiljoer} />
		</React.Fragment>
	)

	return (
		<Panel
			heading={InstutisjonsoppholdPanel.heading}
			startOpen
			informasjonstekst={infoTekst}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
		>
			<AttributtKategori>
				<Attributt attr={sm.attrs.instdata} />
			</AttributtKategori>
		</Panel>
	)
}

InstutisjonsoppholdPanel.heading = 'Instutisjonsopphold'

InstutisjonsoppholdPanel.initialValues = ({ set, del, has }) => ({
	instdata: {
		label: 'Har instutisjonsopphold',
		checked: has('instdata'),
		add() {
			set('instdata', [
				{
					institusjonstype: '',
					startdato: '',
					faktiskSluttdato: ''
				}
			])
		},
		remove() {
			del('instdata')
		}
	}
})
