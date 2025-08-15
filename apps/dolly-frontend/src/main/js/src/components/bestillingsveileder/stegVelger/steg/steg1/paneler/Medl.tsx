import Panel from '@/components/ui/panel/Panel'
import { Attributt } from '../Attributt'
import { AttributtKategori } from '../AttributtKategori'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import { initialMedl } from '@/components/fagsystem/pdlf/form/initialValues'
import { MedlAttributt } from '@/components/fagsystem/medl/MedlConstants'

export const MedlPanel = ({ stateModifier, formValues }: any) => {
	const sm = stateModifier(MedlPanel.initialValues)
	return (
		// @ts-ignore
		<Panel
			heading={MedlPanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="calendar"
			startOpen={harValgtAttributt(formValues, [MedlAttributt])}
		>
			<AttributtKategori title={null} attr={sm.attrs}>
				<Attributt item={sm.attrs.medl} />
			</AttributtKategori>
		</Panel>
	)
}

MedlPanel.heading = 'Medlemskap i folketrygden'

MedlPanel.initialValues = ({ set, del, has }: any) => ({
	medl: {
		label: 'Har perioder i MEDL',
		checked: has('medl'),
		add: () => set('medl', initialMedl),
		remove: () => del('medl'),
	},
})
