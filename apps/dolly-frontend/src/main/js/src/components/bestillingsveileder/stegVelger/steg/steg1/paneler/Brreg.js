import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { harValgtAttributt } from '~/components/ui/form/formUtils'
import { brregAttributt } from '~/components/fagsystem/brregstub/form/Form'

export const BrregPanel = ({ stateModifier, formikBag }) => {
	const sm = stateModifier(BrregPanel.initialValues)

	return (
		<Panel
			heading={BrregPanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="brreg"
			startOpen={harValgtAttributt(formikBag.values, [brregAttributt])}
		>
			<AttributtKategori attr={sm.attrs}>
				<Attributt attr={sm.attrs.brregstub} />
			</AttributtKategori>
		</Panel>
	)
}

BrregPanel.heading = 'Brønnøysundregistrene'

BrregPanel.initialValues = ({ set, del, has }) => ({
	brregstub: {
		label: 'Har roller i Brreg',
		checked: has('brregstub'),
		add() {
			set('brregstub', {
				understatuser: [0],
				enheter: [
					{
						rolle: '',
						registreringsdato: new Date(),
						foretaksNavn: {
							navn1: '',
						},
						orgNr: '',
						personroller: [],
					},
				],
			})
		},
		remove() {
			del('brregstub')
		},
	},
})
