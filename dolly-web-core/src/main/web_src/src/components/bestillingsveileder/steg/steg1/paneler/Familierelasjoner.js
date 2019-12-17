import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import {
	Attributt,
	AttributtKategori
} from '~/components/bestillingsveileder/AttributtVelger/Attributt'

export const FamilierelasjonPanel = ({ stateModifier }) => {
	const sm = stateModifier(FamilierelasjonPanel.initialValues)

	return (
		<Panel
			heading={FamilierelasjonPanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="relasjoner"
		>
			<AttributtKategori title="Partner">
				<Attributt attr={sm.attrs.partner} />
			</AttributtKategori>
			<AttributtKategori title="Barn">
				<Attributt attr={sm.attrs.barn} />
			</AttributtKategori>
		</Panel>
	)
}

FamilierelasjonPanel.heading = 'Familierelasjoner'

FamilierelasjonPanel.initialValues = ({ set, del, has }) => ({
	partner: {
		label: 'Har partner',
		checked: has('tpsf.relasjoner.partnere'),
		add() {
			set('tpsf.relasjoner.partnere', [
				{
					identtype: '',
					kjonn: '',
					sivilstander: { sivilstand: '', sivilstandRegdato: '' },
					harFellesAdresse: false
				}
			])
		},
		remove() {
			del('tpsf.relasjoner.partnere')
		}
	},
	barn: {
		label: 'Har barn',
		checked: has('tpsf.relasjoner.barn'),
		add() {
			set('tpsf.relasjoner.barn', [
				{
					identtype: '',
					kjonn: '',
					barnType: '',
					partnerNr: '',
					borHos: '',
					erAdoptert: false
				}
			])
		},
		remove() {
			del('tpsf.relasjoner.barn')
		}
	}
})
