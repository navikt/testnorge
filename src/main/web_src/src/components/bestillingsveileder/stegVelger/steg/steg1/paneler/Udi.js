import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'

export const UdiPanel = ({ stateModifier }) => {
	const sm = stateModifier(UdiPanel.initialValues)

	const infoTekst =
		'All informasjon blir lagt i UDI-stub. Oppholdsstatus går i tillegg til PDL dersom miljø Q2 velges i siste steg.'

	return (
		<Panel
			heading={UdiPanel.heading}
			informasjonstekst={infoTekst}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="udi"
		>
			<AttributtKategori title="Gjeldende oppholdstatus">
				<Attributt attr={sm.attrs.oppholdStatus} />
			</AttributtKategori>

			<AttributtKategori title="Arbeidsadgang">
				<Attributt attr={sm.attrs.arbeidsadgang} />
				<Attributt attr={sm.attrs.hjemmel} />
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

const arbeidsadgangFelter = {
	arbeidsOmfang: null,
	harArbeidsAdgang: '',
	periode: {
		fra: null,
		til: null
	},
	typeArbeidsadgang: null
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
			set('udistub.arbeidsadgang', arbeidsadgangFelter)
		},
		remove() {
			del('udistub.arbeidsadgang')
		}
	},
	hjemmel: {
		label: 'Innhent vedtakshjemmel',
		checked: has('udistub.arbeidsadgang.hjemmel'),
		add: () =>
			set('udistub.arbeidsadgang', {
				...arbeidsadgangFelter,
				hjemmel: '',
				forklaring: null
			}),
		remove: () => del('udistub.arbeidsadgang.hjemmel')
	},
	aliaser: {
		label: 'Har aliaser',
		checked: has('udistub.aliaser'),
		add: () =>
			set('udistub.aliaser', [
				{
					identtype: null,
					nyIdent: false
				}
			]),
		remove: () => del('udistub.aliaser')
	},
	flyktning: {
		label: 'Flyktningstatus',
		checked: has('udistub.flyktning'),
		add: () => set('udistub.flyktning', null),
		remove: () => del('udistub.flyktning')
	},
	asylsoker: {
		label: 'Asylsøker',
		checked: has('udistub.soeknadOmBeskyttelseUnderBehandling'),
		add: () => set('udistub.soeknadOmBeskyttelseUnderBehandling', ''),
		remove: () => del('udistub.soeknadOmBeskyttelseUnderBehandling')
	}
})
