import React, { useContext } from 'react'
import _get from 'lodash/get'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import Formatters from '~/utils/DataFormatter'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'

export const FamilierelasjonPanel = ({ stateModifier }) => {
	const sm = stateModifier(FamilierelasjonPanel.initialValues)
	const opts = useContext(BestillingsveilederContext)
	const leggTil = opts.is.leggTil

	return (
		<Panel
			heading={FamilierelasjonPanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType={'relasjoner'}
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

FamilierelasjonPanel.initialValues = ({ set, del, has }) => {
	const opts = useContext(BestillingsveilederContext)

	return {
		partner: {
			label: 'Har partner',
			checked: has('tpsf.relasjoner.partnere'),
			add() {
				set('tpsf.relasjoner.partnere', defaultPartner(opts))
			},
			remove() {
				del('tpsf.relasjoner.partnere')
				!has('tpsf.relasjoner.barn') && del('tpsf.relasjoner')
			}
		},
		barn: {
			label: 'Har barn',
			checked: has('tpsf.relasjoner.barn'),
			add() {
				set('tpsf.relasjoner.barn', [
					{
						identtype: 'FNR',
						kjonn: '',
						barnType: '',
						partnerNr: null,
						borHos: '',
						erAdoptert: false,
						alder: Formatters.randomIntInRange(0, 17),
						spesreg: '',
						utenFastBopel: false,
						statsborgerskap: '',
						statsborgerskapRegdato: ''
					}
				])
			},
			remove() {
				del('tpsf.relasjoner.barn')
				!has('tpsf.relasjoner.partnere') && del('tpsf.relasjoner')
			}
		}
	}
}

const defaultPartner = opts => {
	const fullPartner = [
		{
			identtype: 'FNR',
			kjonn: '',
			sivilstander: [{ sivilstand: '', sivilstandRegdato: '' }],
			harFellesAdresse: true,
			alder: Formatters.randomIntInRange(30, 60),
			spesreg: '',
			utenFastBopel: false,
			statsborgerskap: '',
			statsborgerskapRegdato: ''
		}
	]

	const harEksisterendePartner = _get(opts, 'personFoerLeggTil.relasjoner', []).some(
		relasjon => relasjon.relasjonTypeNavn === 'PARTNER'
	)

	return harEksisterendePartner ? [] : fullPartner
}
