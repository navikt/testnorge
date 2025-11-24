import Panel from '@/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import {
	getInitialBarn,
	getInitialForeldreansvar,
	getInitialSivilstand,
	initialDoedfoedtBarn,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import { relasjonerAttributter } from '@/components/fagsystem/pdlf/form/partials/familierelasjoner/Familierelasjoner'
import { useContext } from 'react'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useGruppeIdenter } from '@/utils/hooks/useGruppe'
import { useFormContext } from 'react-hook-form'

export const FamilierelasjonPanel = ({ stateModifier, formValues }) => {
	const formMethods = useFormContext()
	const sm = stateModifier(FamilierelasjonPanel.initialValues)
	const opts: any = useContext(BestillingsveilederContext) as BestillingsveilederContextType

	const testNorgePerson = opts?.identMaster === 'PDL'
	const npidPerson = opts?.identtype === 'NPID'

	const formGruppeId = formMethods.watch('gruppeId')
	const gruppeId = formGruppeId || opts?.gruppeId || opts?.gruppe?.id
	const { identer } = useGruppeIdenter(gruppeId)
	const harTestnorgeIdenter = (identer?.filter((ident) => ident.master === 'PDL')?.length ?? 0) > 0

	const ukjentGruppe = !gruppeId
	const tekstUkjentGruppe = 'Funksjonen er deaktivert da personer for relasjon er ukjent'
	const testNorgeFlere = testNorgePerson && opts?.antall > 1
	const tekstTestNorgeFlere = 'Funksjonen er kun tilgjengelig per individ, ikke for gruppe'
	const leggTilPaaGruppe = !!opts?.leggTilPaaGruppe
	const tekstLeggTilPaaGruppe =
		'Støttes ikke for "legg til på alle" i grupper som inneholder personer fra Test-Norge'

	const foedselsaar =
		opts?.personFoerLeggTil?.pdl?.hentPerson?.foedselsdato?.[0]?.foedselsaar ||
		opts?.personFoerLeggTil?.pdl?.hentPerson?.foedsel?.[0]?.foedselsaar

	const kanHaForeldreansvar = !foedselsaar || new Date().getFullYear() - foedselsaar > 17
	const getIgnoreKeys = () => {
		let ignoreKeys = []

		ignoreKeys.push(!kanHaForeldreansvar ? 'foreldreansvar' : 'ansvarssubjekt')
		if (testNorgePerson || npidPerson) {
			ignoreKeys.push('foreldreansvar', 'doedfoedtBarn')
		}
		if (
			(ukjentGruppe && (testNorgePerson || npidPerson)) ||
			(leggTilPaaGruppe && harTestnorgeIdenter)
		) {
			ignoreKeys.push(
				'sivilstand',
				'barnForeldre',
				'foreldreansvar',
				'ansvarssubjekt',
				'doedfoedtBarn',
			)
		}
		return ignoreKeys
	}

	return (
		<Panel
			heading={FamilierelasjonPanel.heading}
			checkAttributeArray={() => sm.batchAdd(getIgnoreKeys())}
			uncheckAttributeArray={sm.batchRemove}
			iconType={'relasjoner'}
			startOpen={harValgtAttributt(formValues, relasjonerAttributter)}
		>
			<AttributtKategori title="Sivilstand" attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.sivilstand}
					disabled={ukjentGruppe || testNorgeFlere || (leggTilPaaGruppe && harTestnorgeIdenter)}
					title={
						(ukjentGruppe && tekstUkjentGruppe) ||
						(testNorgeFlere && tekstTestNorgeFlere) ||
						(leggTilPaaGruppe && harTestnorgeIdenter && tekstLeggTilPaaGruppe) ||
						''
					}
				/>
			</AttributtKategori>
			<AttributtKategori title="Barn/foreldre" attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.barnForeldre}
					disabled={ukjentGruppe || testNorgeFlere || (leggTilPaaGruppe && harTestnorgeIdenter)}
					title={
						(ukjentGruppe && tekstUkjentGruppe) ||
						(testNorgeFlere && tekstTestNorgeFlere) ||
						(leggTilPaaGruppe && harTestnorgeIdenter && tekstLeggTilPaaGruppe) ||
						''
					}
				/>
				<Attributt
					attr={kanHaForeldreansvar ? sm.attrs.foreldreansvar : sm.attrs.ansvarssubjekt}
					disabled={npidPerson || (leggTilPaaGruppe && harTestnorgeIdenter)}
					title={
						(npidPerson && 'Ikke tilgjengelig for personer med identtype NPID') ||
						(leggTilPaaGruppe && harTestnorgeIdenter && tekstLeggTilPaaGruppe) ||
						''
					}
					vis={!testNorgePerson}
				/>
			</AttributtKategori>
			<AttributtKategori title="Dødfødt barn" attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.doedfoedtBarn}
					disabled={npidPerson || (leggTilPaaGruppe && harTestnorgeIdenter)}
					title={
						(npidPerson && 'Ikke tilgjengelig for personer med identtype NPID') ||
						(leggTilPaaGruppe && harTestnorgeIdenter && tekstLeggTilPaaGruppe) ||
						''
					}
					vis={!testNorgePerson}
				/>
			</AttributtKategori>
		</Panel>
	)
}

FamilierelasjonPanel.heading = 'Familierelasjoner'

FamilierelasjonPanel.initialValues = ({ set, opts, del, has }: any) => {
	const { identtype, identMaster, personFoerLeggTil } = opts
	const initialMaster = identMaster === 'PDL' || identtype === 'NPID' ? 'PDL' : 'FREG'

	const foedselsaar =
		personFoerLeggTil?.pdl?.hentPerson?.foedselsdato?.[0]?.foedselsaar ||
		personFoerLeggTil?.pdl?.hentPerson?.foedsel?.[0]?.foedselsaar

	const kanHaForeldreansvar = !foedselsaar || new Date().getFullYear() - foedselsaar > 17

	return {
		sivilstand: {
			label: 'Sivilstand (har partner)',
			checked: has('pdldata.person.sivilstand'),
			add() {
				set('pdldata.person.sivilstand', [getInitialSivilstand(initialMaster)])
			},
			remove() {
				del('pdldata.person.sivilstand')
			},
		},
		barnForeldre: {
			label: 'Har barn/foreldre',
			checked: has('pdldata.person.forelderBarnRelasjon'),
			add() {
				set('pdldata.person.forelderBarnRelasjon', [getInitialBarn(initialMaster)])
			},
			remove() {
				del('pdldata.person.forelderBarnRelasjon')
			},
		},
		foreldreansvar: {
			label: 'Har foreldreansvar',
			checked: has('pdldata.person.foreldreansvar[0].harForeldreansvar'),
			add() {
				set('pdldata.person.foreldreansvar', [getInitialForeldreansvar(kanHaForeldreansvar)])
			},
			remove() {
				del('pdldata.person.foreldreansvar')
			},
		},
		ansvarssubjekt: {
			label: 'Er ansvarssubjekt (foreldreansvar)',
			checked: has('pdldata.person.foreldreansvar[0].erAnsvarssubjekt'),
			add() {
				set('pdldata.person.foreldreansvar', [getInitialForeldreansvar(kanHaForeldreansvar)])
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
	}
}
