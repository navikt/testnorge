import React from 'react'
import _get from 'lodash/get'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { initialBarn, initialSivilstand } from '~/components/fagsystem/pdlf/form/initialValues'

export const FamilierelasjonPanel = ({ stateModifier }) => {
	const sm = stateModifier(FamilierelasjonPanel.initialValues)

	return (
		<Panel
			heading={FamilierelasjonPanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType={'relasjoner'}
		>
			<AttributtKategori title="Sivilstand">
				<Attributt attr={sm.attrs.sivilstand} />
			</AttributtKategori>
			<AttributtKategori title="Barn/Foreldre">
				<Attributt attr={sm.attrs.barnForeldre} />
			</AttributtKategori>
		</Panel>
	)
}

FamilierelasjonPanel.heading = 'Familierelasjoner'

FamilierelasjonPanel.initialValues = ({ set, del, has, opts }) => ({
	sivilstand: {
		label: 'Sivilstand (har partner)',
		checked: has('pdldata.person.sivilstand'),
		add() {
			set('pdldata.person.sivilstand', [initialSivilstand])
		},
		remove() {
			del('pdldata.person.sivilstand')
		},
	},
	barnForeldre: {
		label: 'Har barn/foreldre',
		checked: has('pdldata.person.forelderBarnRelasjon'),
		add() {
			set('pdldata.person.forelderBarnRelasjon', defaultBarn(opts)?.concat(defaultForeldre(opts)))
		},
		remove() {
			del('pdldata.person.forelderBarnRelasjon')
		},
	},
})

const defaultForeldre = (opts) => {
	const eksisterendeRelasjoner = _get(opts, 'personFoerLeggTil.tpsf.relasjoner') // TODO: Sjekke om denne fungerer
	const eksisterendeForeldre =
		eksisterendeRelasjoner &&
		eksisterendeRelasjoner.filter(
			(relasjon) => relasjon.relasjonTypeNavn === 'MOR' || relasjon.relasjonTypeNavn === 'FAR'
		)
	const eksisterendeForelderValues =
		eksisterendeForeldre &&
		eksisterendeForeldre.map((forelder) => ({
			ident: forelder.personRelasjonMed.ident,
			doedsdato: forelder.personRelasjonMed.doedsdato || null,
		}))

	return eksisterendeForelderValues && eksisterendeForelderValues.length > 0
		? eksisterendeForelderValues
		: [initialBarn]
}

const defaultBarn = (opts) => {
	const eksisterendeRelasjoner = _get(opts, 'personFoerLeggTil.tpsf.relasjoner')
	const eksisterendeBarn =
		eksisterendeRelasjoner &&
		eksisterendeRelasjoner.filter((relasjon) => relasjon.relasjonTypeNavn === 'FOEDSEL')
	const eksisterendeBarnValues =
		eksisterendeBarn &&
		eksisterendeBarn.map((barn) => ({
			ident: barn.personRelasjonMed.ident,
			doedsdato: barn.personRelasjonMed.doedsdato || null,
		}))

	return eksisterendeBarnValues && eksisterendeBarnValues.length > 0 ? eksisterendeBarnValues : []
}
