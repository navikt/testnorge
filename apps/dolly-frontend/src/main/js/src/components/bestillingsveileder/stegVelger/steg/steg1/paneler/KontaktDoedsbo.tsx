import Panel from '@/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { initialKontaktinfoForDoedebo } from '@/components/fagsystem/pdlf/form/initialValues'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import { doedsboAttributt } from '@/components/fagsystem/pdlf/form/partials/kontaktinformasjonForDoedsbo/KontaktinformasjonForDoedsbo'
import { useContext } from 'react'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'

export const KontaktDoedsboPanel = ({ stateModifier, formikBag }) => {
	const sm = stateModifier(KontaktDoedsboPanel.initialValues)
	const opts = useContext(BestillingsveilederContext)

	const getIgnoreKeys = () => {
		if (opts?.identtype === 'NPID') {
			return ['kontaktinformasjonForDoedsbo']
		}
		return []
	}

	return (
		<Panel
			heading={KontaktDoedsboPanel.heading}
			checkAttributeArray={() => sm.batchAdd(getIgnoreKeys())}
			uncheckAttributeArray={sm.batchRemove}
			iconType="doedsbo"
			startOpen={harValgtAttributt(formikBag.values, [doedsboAttributt])}
		>
			<AttributtKategori attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.kontaktinformasjonForDoedsbo}
					disabled={opts?.identtype === 'NPID'}
					title={
						opts?.identtype === 'NPID' ? 'Ikke tilgjengelig for personer med identtype NPID' : ''
					}
				/>
			</AttributtKategori>
		</Panel>
	)
}

KontaktDoedsboPanel.heading = 'Kontaktinformasjon for dødsbo'

KontaktDoedsboPanel.initialValues = ({ set, del, has }) => ({
	kontaktinformasjonForDoedsbo: {
		label: 'Har kontaktinformasjon for dødsbo',
		checked: has('pdldata.person.kontaktinformasjonForDoedsbo'),
		add() {
			set('pdldata.person.kontaktinformasjonForDoedsbo', [initialKontaktinfoForDoedebo])
		},
		remove() {
			del('pdldata.person.kontaktinformasjonForDoedsbo')
		},
	},
})
