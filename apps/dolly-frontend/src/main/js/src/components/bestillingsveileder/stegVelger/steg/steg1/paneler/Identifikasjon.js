import React, { useContext } from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { initialNyIdent } from '~/components/fagsystem/pdlf/form/initialValues'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import { harValgtAttributt } from '~/components/ui/form/formUtils'
import { identifikasjonAttributter } from '~/components/fagsystem/pdlf/form/partials/identifikasjon/Identifikasjon'

export const IdentifikasjonPanel = ({ stateModifier, formikBag }) => {
	const sm = stateModifier(IdentifikasjonPanel.initialValues)
	const opts = useContext(BestillingsveilederContext)

	const harNpid = opts.identtype === 'NPID'
	const harTpsfMaster = opts.identMaster === 'TPSF'

	const nyIdentTitle = () => {
		if (harNpid) return 'Personer med identtype NPID kan ikke ha identhistorikk'
		if (harTpsfMaster) return 'Personer med TPS som master kan ikke få ny ident'
		return ''
	}

	return (
		<Panel
			heading={IdentifikasjonPanel.heading}
			checkAttributeArray={() => sm.batchAdd((harNpid || harTpsfMaster) && 'nyident')}
			uncheckAttributeArray={sm.batchRemove}
			iconType="identifikasjon"
			startOpen={harValgtAttributt(formikBag.values, identifikasjonAttributter)}
		>
			<AttributtKategori>
				<Attributt attr={sm.attrs.falskIdentitet} />
				<Attributt attr={sm.attrs.utenlandskIdentifikasjonsnummer} />
				<Attributt
					attr={sm.attrs.nyident}
					disabled={harNpid || harTpsfMaster}
					title={nyIdentTitle()}
				/>
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
					kilde: 'Dolly',
					master: 'FREG',
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
				{
					identifikasjonsnummer: '',
					opphoert: false,
					utstederland: '',
					kilde: 'Dolly',
					master: 'FREG',
				},
			]),
		remove: () => del('pdldata.person.utenlandskIdentifikasjonsnummer'),
	},
	nyident: {
		label: 'Har ny ident',
		checked: has('pdldata.person.nyident'),
		add() {
			set('pdldata.person.nyident', [initialNyIdent])
		},
		remove() {
			del('pdldata.person.nyident')
		},
	},
})
