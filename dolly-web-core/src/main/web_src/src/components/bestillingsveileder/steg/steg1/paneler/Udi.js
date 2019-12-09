import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import {
	Attributt,
	AttributtKategori
} from '~/components/bestillingsveileder/AttributtVelger/Attributt'

export const UdiPanel = ({ stateModifier }) => {
	const sm = stateModifier(UdiPanel.initialValues)

	return (
		<Panel
			heading={UdiPanel.heading}
			startOpen
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
		>
			<AttributtKategori title="Gjeldende oppholdstatus">
				<Attributt attr={sm.attrs.oppholdStatus} />
			</AttributtKategori>

			<AttributtKategori title="Arbeidsadgang">
				<Attributt attr={sm.attrs.arbeidsadgang} />
			</AttributtKategori>

			<AttributtKategori title="Alias">
				<Attributt attr={sm.attrs.aliaser} />
			</AttributtKategori>

			<AttributtKategori title="Annet">
				<Attributt attr={sm.attrs.flyktning} />
				<Attributt attr={sm.attrs.asylsoker} />
			</AttributtKategori>
		</Panel>
	)
}

UdiPanel.heading = 'UDI'

UdiPanel.initialValues = ({ set, del, has }) => ({
	oppholdStatus: {
		label: 'Oppholdstatus',
		checked: has('udistub.oppholdStatus'),
		add: () => set('udistub.oppholdStatus', {}),
		remove: () => del('udistub.oppholdStatus')
	},
	arbeidsadgang: {
		label: 'Arbeidsadgang',
		checked: has('udistub.arbeidsadgang'),
		add() {
			set('udistub.arbeidsadgang', {
				arbeidsOmfang: '',
				harArbeidsAdgang: '',
				periode: {
					fra: '',
					til: ''
				},
				typeArbeidsadgang: ''
			})
		},
		remove() {
			del('udistub.arbeidsadgang')
		}
	},
	aliaser: {
		label: 'Har aliaser',
		checked: has('udistub.aliaser'),
		add: () =>
			set('udistub.aliaser', [
				{
					identtype: '',
					nyIdent: false
				}
			]),
		remove: () => del('udistub.aliaser')
	},
	flyktning: {
		label: 'Flyktningstatus',
		checked: has('udistub.flyktning'),
		add: () => set('udistub.flyktning', ''),
		remove: () => del('udistub.flyktning')
	},
	asylsoker: {
		label: 'Asylsøker',
		checked: has('udistub.soeknadOmBeskyttelseUnderBehandling'),
		add: () => set('udistub.soeknadOmBeskyttelseUnderBehandling', ''),
		remove: () => del('udistub.soeknadOmBeskyttelseUnderBehandling')
	}
})
