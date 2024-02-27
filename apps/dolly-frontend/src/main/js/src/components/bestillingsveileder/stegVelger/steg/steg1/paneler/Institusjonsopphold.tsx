import Panel from '@/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import { instAttributt } from '@/components/fagsystem/inst/form/Form'
import { runningCypressE2E } from '@/service/services/Request'

export const InstitusjonsoppholdPanel = ({ stateModifier, formValues }) => {
	const sm = stateModifier(InstitusjonsoppholdPanel.initialValues)

	return (
		<Panel
			heading={InstitusjonsoppholdPanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="institusjon"
			startOpen={harValgtAttributt(formValues, [instAttributt])}
		>
			<AttributtKategori attr={sm.attrs}>
				<Attributt attr={sm.attrs.instdata} />
			</AttributtKategori>
		</Panel>
	)
}

InstitusjonsoppholdPanel.heading = 'Institusjonsopphold'

InstitusjonsoppholdPanel.initialValues = ({ set, del, has }) => ({
	instdata: {
		label: 'Har institusjonsopphold',
		checked: has('instdata'),
		add() {
			set('instdata', [
				{
					institusjonstype: runningCypressE2E() ? 'AS' : '',
					startdato: runningCypressE2E() ? new Date() : '',
					sluttdato: '',
				},
			])
		},
		remove() {
			del('instdata')
		},
	},
})
