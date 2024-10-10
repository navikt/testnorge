import Panel from '@/components/ui/panel/Panel'
import {
	Attributt,
	AttributtKategori,
} from '@/components/bestillingsveileder/stegVelger/steg/steg1/Attributt'
import { initialFullmakt, initialPdlPerson } from '@/components/fagsystem/pdlf/form/initialValues'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import React, { useContext } from 'react'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { fullmaktAttributter } from '@/components/fagsystem/fullmakt/form/FullmaktForm'

export const FullmaktPanel = ({ stateModifier, formValues }: any) => {
	const sm = stateModifier(FullmaktPanel.initialValues)
	const opts = useContext(BestillingsveilederContext)
	const gruppeId = opts?.gruppeId || opts?.gruppe?.id

	const testNorgePerson = opts?.identMaster === 'PDL'
	const ukjentGruppe = !gruppeId
	const tekstUkjentGruppe = 'Funksjonen er deaktivert da personer for relasjon er ukjent'
	const testNorgeFlere = testNorgePerson && opts?.antall > 1
	const tekstFlerePersoner = 'Funksjonen er kun tilgjengelig per individ, ikke for gruppe'

	return (
		// @ts-ignore
		<Panel
			heading={FullmaktPanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="fullmakt"
			startOpen={harValgtAttributt(formValues, fullmaktAttributter)}
		>
			<AttributtKategori title="Fullmakt" attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.fullmakt}
					disabled={ukjentGruppe || testNorgeFlere}
					title={
						(ukjentGruppe && tekstUkjentGruppe) || (testNorgeFlere && tekstFlerePersoner) || ''
					}
				/>
			</AttributtKategori>
		</Panel>
	)
}

FullmaktPanel.heading = 'Fullmakt'

FullmaktPanel.initialValues = ({ set, opts, del, has }: any) => {
	const { identtype, identMaster } = opts

	const initialMaster = identMaster === 'PDL' || identtype === 'NPID' ? 'PDL' : 'FREG'
	const initialValues = { ...initialFullmakt, master: initialMaster }

	return {
		fullmakt: {
			label: 'Har fullmektig',
			checked: has('fullmakt') || has('pdldata.person.fullmakt'),
			add: () => {
				set('fullmakt', [initialValues])
				set('pdldata.person.fullmakt', [{ nyfullMektig: initialPdlPerson }])
			},
			remove: () => {
				del('fullmakt')
				del('pdldata.person.fullmakt')
			},
		},
	}
}
