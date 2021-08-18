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
			<AttributtKategori title="Identifikasjon">
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
		checked: has('pdlforvalter.falskIdentitet'),
		add() {
			set('pdlforvalter.falskIdentitet', {
				rettIdentitet: { identitetType: 'UKJENT', rettIdentitetErUkjent: true }
			})
		},
		remove() {
			del('pdlforvalter.falskIdentitet')
		}
	},
	utenlandskIdentifikasjonsnummer: {
		label: 'Har utenlandsk ID',
		checked: has('pdlforvalter.utenlandskIdentifikasjonsnummer'),
		add: () =>
			set('pdlforvalter.utenlandskIdentifikasjonsnummer', [
				{ identifikasjonsnummer: '', kilde: '', opphoert: '', utstederland: '' }
			]),
		remove: () => del('pdlforvalter.utenlandskIdentifikasjonsnummer')
	}
})
