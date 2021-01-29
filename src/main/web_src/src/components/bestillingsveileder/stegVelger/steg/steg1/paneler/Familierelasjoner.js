import React from 'react'
import _get from 'lodash/get'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import Formatters from '~/utils/DataFormatter'

export const FamilierelasjonPanel = ({ stateModifier }) => {
	const sm = stateModifier(FamilierelasjonPanel.initialValues)

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

FamilierelasjonPanel.initialValues = ({ set, del, has, opts }) => ({
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
			set('tpsf.relasjoner.barn', defaultBarn(opts))
		},
		remove() {
			del('tpsf.relasjoner.barn')
			!has('tpsf.relasjoner.partnere') && del('tpsf.relasjoner')
		}
	}
})

const defaultPartner = opts => {
	const fullPartner = [
		{
			identtype: 'FNR',
			kjonn: '',
			sivilstander: [{ sivilstand: '', sivilstandRegdato: '' }],
			harFellesAdresse: true,
			alder: Formatters.randomIntInRange(30, 60),
			doedsdato: null,
			spesreg: '',
			utenFastBopel: false,
			statsborgerskap: '',
			statsborgerskapRegdato: '',
			statsborgerskapTildato: ''
		}
	]

	const eksisterendePartner = [
		{
			ident: _get(opts, 'personFoerLeggTil.tpsf.relasjoner[0].personRelasjonMed.ident'),
			doedsdato:
				_get(opts, 'personFoerLeggTil.tpsf.relasjoner[0].personRelasjonMed.doedsdato') || null,
			sivilstander: []
		}
	]

	const harEksisterendePartner = _get(opts, 'personFoerLeggTil.tpsf.relasjoner', []).some(
		relasjon => relasjon.relasjonTypeNavn === 'PARTNER'
	)

	return harEksisterendePartner ? eksisterendePartner : fullPartner
}

const defaultBarn = opts => {
	const fullBarn = [
		{
			identtype: 'FNR',
			kjonn: '',
			barnType: '',
			partnerNr: null,
			borHos: '',
			erAdoptert: false,
			alder: Formatters.randomIntInRange(0, 17),
			doedsdato: null,
			spesreg: '',
			utenFastBopel: false,
			statsborgerskap: '',
			statsborgerskapRegdato: '',
			statsborgerskapTildato: ''
		}
	]

	const eksisterendeRelasjoner = _get(opts, 'personFoerLeggTil.tpsf.relasjoner')
	const eksisterendeBarn =
		eksisterendeRelasjoner &&
		eksisterendeRelasjoner.filter(relasjon => relasjon.relasjonTypeNavn === 'FOEDSEL')
	const eksisterendeBarnValues =
		eksisterendeBarn &&
		eksisterendeBarn.map(barn => ({
			ident: barn.personRelasjonMed.ident,
			doedsdato: barn.personRelasjonMed.doedsdato || null
		}))

	return eksisterendeBarnValues && eksisterendeBarnValues.length > 0
		? eksisterendeBarnValues
		: fullBarn
}
