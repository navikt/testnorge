import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import {
	Attributt,
	AttributtKategori
} from '~/components/bestillingsveileder/AttributtVelger/Attributt'

export const KontaktReservasjonsPanel = ({ stateModifier }) => {
	const sm = stateModifier(KontaktReservasjonsPanel.initialValues)

	return (
		<Panel
			heading={KontaktReservasjonsPanel.heading}
			startOpen
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
		>
			<AttributtKategori>
				<Attributt attr={sm.attrs.krrstub} />
			</AttributtKategori>
		</Panel>
	)
}

KontaktReservasjonsPanel.heading = 'Kontakt- og reservasjonsregisteret'

KontaktReservasjonsPanel.initialValues = ({ set, del, has }) => ({
	krrstub: {
		label: 'Har kontaktinformasjon',
		checked: has('krrstub'),
		add() {
			set('krrstub', {
				epost: '',
				gyldigFra: new Date(),
				mobil: '',
				registrert: true,
				reservert: false
			})
		},
		remove() {
			del('krrstub')
		}
	}
})
