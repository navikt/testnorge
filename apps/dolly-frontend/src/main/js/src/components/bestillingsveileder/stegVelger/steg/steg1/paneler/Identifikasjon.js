import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'

export const IdentifikasjonPanel = ({ stateModifier }) => {
	const sm = stateModifier(IdentifikasjonPanel.initialValues)

	return (
		<Panel
			heading={IdentifikasjonPanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="identifikasjon"
		>
			<AttributtKategori>
				<Attributt attr={sm.attrs.falskIdentitet} />
				<Attributt attr={sm.attrs.utenlandskIdentifikasjonsnummer} />
			</AttributtKategori>
		</Panel>
	)
}

IdentifikasjonPanel.heading = 'Identifikasjon'

IdentifikasjonPanel.initialValues = ({ set, del, has }) => ({
	falskIdentitet: {
		label: 'Har falsk identitet',
		checked: has('pdldata.person.falskIdentitet'),
		add() {
			set('pdldata.person.falskIdentitet', [
				{
					erFalsk: true,
				},
			])
		},
		remove() {
			del('pdldata.person.falskIdentitet')
		},
	},
	utenlandskIdentifikasjonsnummer: {
		label: 'Har utenlandsk ID',
		checked: has('pdldata.person.utenlandskIdentifikasjonsnummer'),
		add: () =>
			set('pdldata.person.utenlandskIdentifikasjonsnummer', [
				{ identifikasjonsnummer: '', kilde: '', opphoert: '', utstederland: '' },
			]),
		remove: () => del('pdldata.person.utenlandskIdentifikasjonsnummer'),
	},
})
