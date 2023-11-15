import Panel from '@/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import {
	initialBarn,
	initialDoedfoedtBarn,
	initialForeldreansvar,
	initialSivilstand,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import { relasjonerAttributter } from '@/components/fagsystem/pdlf/form/partials/familierelasjoner/Familierelasjoner'
import { useContext } from 'react'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'

export const FamilierelasjonPanel = ({ stateModifier, formikBag }) => {
	const sm = stateModifier(FamilierelasjonPanel.initialValues)
	const opts = useContext(BestillingsveilederContext)

	const getIgnoreKeys = () => {
		if (opts?.identtype === 'NPID') {
			return ['foreldreansvar', 'doedfoedtBarn']
		}
		return []
	}

	return (
		<Panel
			heading={FamilierelasjonPanel.heading}
			checkAttributeArray={() => sm.batchAdd(getIgnoreKeys())}
			uncheckAttributeArray={sm.batchRemove}
			iconType={'relasjoner'}
			startOpen={harValgtAttributt(formikBag.values, relasjonerAttributter)}
		>
			<AttributtKategori title="Sivilstand" attr={sm.attrs}>
				<Attributt attr={sm.attrs.sivilstand} />
			</AttributtKategori>
			<AttributtKategori title="Barn/foreldre" attr={sm.attrs}>
				<Attributt attr={sm.attrs.barnForeldre} />
				<Attributt
					attr={sm.attrs.foreldreansvar}
					disabled={opts?.identtype === 'NPID'}
					title={
						opts?.identtype === 'NPID' ? 'Ikke tilgjengelig for personer med identtype NPID' : ''
					}
				/>
			</AttributtKategori>
			<AttributtKategori title="Dødfødt barn" attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.doedfoedtBarn}
					disabled={opts?.identtype === 'NPID'}
					title={
						opts?.identtype === 'NPID' ? 'Ikke tilgjengelig for personer med identtype NPID' : ''
					}
				/>
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
