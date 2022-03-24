import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import {
	initialBarn,
	initialDoedfoedtBarn,
	initialForeldreansvar,
	initialSivilstand,
} from '~/components/fagsystem/pdlf/form/initialValues'

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
			<AttributtKategori title="Barn/foreldre">
				<Attributt attr={sm.attrs.barnForeldre} />
				<Attributt attr={sm.attrs.foreldreansvar} />
			</AttributtKategori>
			<AttributtKategori title="Dødfødt barn">
				<Attributt attr={sm.attrs.doedfoedtBarn} />
			</AttributtKategori>
		</Panel>
	)
}

FamilierelasjonPanel.heading = 'Familierelasjoner'

FamilierelasjonPanel.initialValues = ({ set, del, has }) => ({
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
			set('pdldata.person.forelderBarnRelasjon', [initialBarn])
		},
		remove() {
			del('pdldata.person.forelderBarnRelasjon')
		},
	},
	foreldreansvar: {
		label: 'Har foreldreansvar',
		checked: has('pdldata.person.foreldreansvar'),
		add() {
			set('pdldata.person.foreldreansvar', [initialForeldreansvar])
		},
		remove() {
			del('pdldata.person.foreldreansvar')
		},
	},
	doedfoedtBarn: {
		label: 'Har dødfødt barn',
		checked: has('pdldata.person.doedfoedtBarn'),
		add() {
			set('pdldata.person.doedfoedtBarn', [initialDoedfoedtBarn])
		},
		remove() {
			del('pdldata.person.doedfoedtBarn')
		},
	},
})
