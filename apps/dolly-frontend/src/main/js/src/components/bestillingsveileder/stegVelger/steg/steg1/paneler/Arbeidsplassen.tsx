import { harValgtAttributt } from '@/components/ui/form/formUtils'
import {
	Attributt,
	AttributtKategori,
} from '@/components/bestillingsveileder/stegVelger/steg/steg1/Attributt'
import Panel from '@/components/ui/panel/Panel'
import { initialCV } from '@/components/fagsystem/arbeidsplassen/form/initialValues'

export const ArbeidsplassenPanel = ({ stateModifier, formikBag }) => {
	const sm = stateModifier(ArbeidsplassenPanel.initialValues)
	return (
		<Panel
			heading={ArbeidsplassenPanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="cv"
			startOpen={harValgtAttributt(formikBag.values, ['arbeidsplassenCV'])}
		>
			<AttributtKategori title={null} attr={sm.attrs}>
				<Attributt attr={sm.attrs.arbeidsplassen} />
			</AttributtKategori>
		</Panel>
	)
}

ArbeidsplassenPanel.heading = 'Arbeidsplassen (CV)'

ArbeidsplassenPanel.initialValues = ({ set, del, has }) => ({
	arbeidsplassen: {
		label: 'Har CV',
		checked: has('arbeidsplassenCV'),
		add() {
			set('arbeidsplassenCV', initialCV)
		},
		remove() {
			del('arbeidsplassenCV')
		},
	},
})
