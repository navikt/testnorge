import React, { useContext } from 'react'
import Panel from '@/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import {
	getInitialNyIdent,
	getInitialUtenlandskIdentifikasjonsnummer,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import { identifikasjonAttributter } from '@/components/fagsystem/pdlf/form/partials/identifikasjon/Identifikasjon'

export const IdentifikasjonPanel = ({ stateModifier, formValues }) => {
	const sm = stateModifier(IdentifikasjonPanel.initialValues)
	const opts = useContext(BestillingsveilederContext)

	const harNpid = opts.identtype === 'NPID'

	return (
		<Panel
			heading={IdentifikasjonPanel.heading}
			checkAttributeArray={() => sm.batchAdd(harNpid ? ['falskIdentitet'] : [])}
			uncheckAttributeArray={sm.batchRemove}
			iconType="identifikasjon"
			startOpen={harValgtAttributt(formValues, identifikasjonAttributter)}
		>
			<AttributtKategori attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.falskIdentitet}
					disabled={harNpid}
					title={harNpid ? 'Personer med identtype NPID kan ikke ha falsk identitet' : ''}
				/>
				<Attributt attr={sm.attrs.utenlandskIdentifikasjonsnummer} />
				<Attributt attr={sm.attrs.nyident} />
			</AttributtKategori>
		</Panel>
	)
}

IdentifikasjonPanel.heading = 'Identifikasjon'

IdentifikasjonPanel.initialValues = ({ set, del, has }) => {
	const opts = useContext(BestillingsveilederContext)
	const { identtype } = opts
	return {
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
					getInitialUtenlandskIdentifikasjonsnummer(identtype === 'NPID' ? 'PDL' : 'FREG'),
				]),
			remove: () => del('pdldata.person.utenlandskIdentifikasjonsnummer'),
		},
		nyident: {
			label: 'Har ny ident',
			checked: has('pdldata.person.nyident'),
			add() {
				set('pdldata.person.nyident', [getInitialNyIdent(identtype === 'NPID' ? 'PDL' : 'FREG')])
			},
			remove() {
				del('pdldata.person.nyident')
			},
		},
	}
}
