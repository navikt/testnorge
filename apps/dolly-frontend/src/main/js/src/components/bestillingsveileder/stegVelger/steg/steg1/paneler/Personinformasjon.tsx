import React, { useContext } from 'react'
import * as _ from 'lodash-es'
import Panel from '@/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import {
	getInitialDoedsfall,
	getInitialFoedested,
	getInitialFoedselsdato,
	getInitialKjoenn,
	getInitialNavn,
	getInitialStatsborgerskap,
	initialFullmakt,
	initialPdlPerson,
	initialSikkerhetstiltak,
	initialTilrettelagtKommunikasjon,
	initialVergemaal,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { useGruppeIdenter } from '@/utils/hooks/useGruppe'
import { useFormContext } from 'react-hook-form'

const ignoreKeysTestnorge = [
	'alder',
	'innvandretFraLand',
	'utvandretTilLand',
	'identtype',
	'vergemaal',
]

const utvandret = 'utvandretTilLand'

// @ts-ignore
export const PersoninformasjonPanel = ({ stateModifier, testnorgeIdent }) => {
	const formMethods = useFormContext()
	const sm: any = stateModifier(PersoninformasjonPanel.initialValues)
	const opts: any = useContext(BestillingsveilederContext) as BestillingsveilederContextType

	const formGruppeId = formMethods.watch('gruppeId')
	const gruppeId = formGruppeId || opts?.gruppeId || opts?.gruppe?.id
	const { identer } = useGruppeIdenter(gruppeId)
	const harTestnorgeIdenter = (identer?.filter((ident) => ident.master === 'PDL')?.length ?? 0) > 0

	const opprettFraEksisterende = opts?.is?.opprettFraIdenter
	const leggTil = opts?.is?.leggTil || opts?.is?.leggTilPaaGruppe

	const npidPerson = opts?.identtype === 'NPID'
	const ukjentGruppe = !gruppeId
	const leggTilPaaGruppe = !!opts?.leggTilPaaGruppe
	const tekstLeggTilPaaGruppe =
		'Støttes ikke for "legg til på alle" i grupper som inneholder personer fra Test-Norge'
	const tekstUkjentGruppe = 'Funksjonen er deaktivert da personer for relasjon er ukjent'

	const harFnr = opts?.identtype === 'FNR'
	// Noen egenskaper kan ikke endres når personen opprettes fra eksisterende eller videreføres med legg til

	const getIgnoreKeys = () => {
		let ignoreKeys = testnorgeIdent ? [...ignoreKeysTestnorge] : ['identtype']
		if (
			(testnorgeIdent && (ukjentGruppe || opts?.antall > 1)) ||
			(harTestnorgeIdenter && leggTilPaaGruppe)
		) {
			ignoreKeys.push('vergemaal', 'innvandretFraLand', 'utvandretTilLand')
		}
		if (!testnorgeIdent && !harFnr) {
			ignoreKeys.push(utvandret)
		}
		if (!harFnr) {
			ignoreKeys.push('innvandretFraLand', 'vergemaal')
		}
		return ignoreKeys
	}

	return (
		<Panel
			heading={PersoninformasjonPanel.heading}
			startOpen
			checkAttributeArray={() => sm.batchAdd(getIgnoreKeys())}
			uncheckAttributeArray={sm.batchRemove}
			iconType={'personinformasjon'}
		>
			<AttributtKategori title="Alder" attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.alder}
					vis={!testnorgeIdent && !opprettFraEksisterende && !leggTil}
				/>
				<Attributt attr={sm.attrs.foedested} vis={true} />
				<Attributt attr={sm.attrs.foedselsdato} vis={true} />
				<Attributt attr={sm.attrs.doedsdato} vis={true} />
			</AttributtKategori>

			<AttributtKategori title="Nasjonalitet" attr={sm.attrs}>
				<Attributt attr={sm.attrs.statsborgerskap} vis={true} />
				<Attributt
					attr={sm.attrs.innvandretFraLand}
					vis={!testnorgeIdent}
					disabled={!harFnr || (harTestnorgeIdenter && leggTilPaaGruppe)}
					title={
						(!harFnr &&
							'Personer med identtype DNR eller NPID kan ikke innvandre fordi de ikke har norsk statsborgerskap') ||
						(harTestnorgeIdenter && leggTilPaaGruppe && tekstLeggTilPaaGruppe) ||
						''
					}
				/>
				<Attributt
					attr={sm.attrs.utvandretTilLand}
					vis={!testnorgeIdent}
					disabled={!harFnr || (harTestnorgeIdenter && leggTilPaaGruppe)}
					title={
						(!harFnr &&
							'Personer med identtype DNR eller NPID kan ikke utvandre fordi de ikke har norsk statsborgerskap') ||
						(harTestnorgeIdenter && leggTilPaaGruppe && tekstLeggTilPaaGruppe) ||
						''
					}
				/>
			</AttributtKategori>
			<AttributtKategori title="Diverse" attr={sm.attrs}>
				<Attributt attr={sm.attrs.kjonn} vis={!opprettFraEksisterende} />
				<Attributt attr={sm.attrs.navn} vis={true} />
				<Attributt attr={sm.attrs.telefonnummer} vis={true} />
				<Attributt
					attr={sm.attrs.vergemaal}
					vis={!testnorgeIdent}
					disabled={npidPerson || (harTestnorgeIdenter && leggTilPaaGruppe)}
					title={
						(npidPerson && 'Ikke tilgjengelig for personer med identtype NPID') ||
						(harTestnorgeIdenter && leggTilPaaGruppe && tekstLeggTilPaaGruppe) ||
						''
					}
				/>
				<Attributt
					attr={sm.attrs.fullmakt}
					vis={true}
					disabled={testnorgeIdent && ukjentGruppe}
					title={(testnorgeIdent && ukjentGruppe && tekstUkjentGruppe) || ''}
				/>
				<Attributt attr={sm.attrs.sikkerhetstiltak} vis={true} />
				<Attributt attr={sm.attrs.tilrettelagtKommunikasjon} vis={true} />
			</AttributtKategori>
		</Panel>
	)
}

PersoninformasjonPanel.heading = 'Personinformasjon'

// @ts-ignore
PersoninformasjonPanel.initialValues = ({ set, opts, setMulti, del, has }) => {
	const { personFoerLeggTil, identtype, identMaster } = opts

	const initMaster = identMaster === 'PDL' || identtype === 'NPID' ? 'PDL' : 'FREG'

	const fjernIdFoerLeggTil = (path: string) => {
		const pdlDataElement = _.get(personFoerLeggTil, `pdlforvalter.person.${path}`)
		return pdlDataElement.map(({ id, ...restProperties }: { id: string }) => restProperties)
	}

	const paths = {
		alder: 'pdldata.opprettNyPerson.alder',
		foedtEtter: 'pdldata.opprettNyPerson.foedtEtter',
		foedtFoer: 'pdldata.opprettNyPerson.foedtFoer',
		foedested: 'pdldata.person.foedested',
		foedselsdato: 'pdldata.person.foedselsdato',
		doedsfall: 'pdldata.person.doedsfall',
		statsborgerskap: 'pdldata.person.statsborgerskap',
		innflytting: 'pdldata.person.innflytting',
		utflytting: 'pdldata.person.utflytting',
		kjoenn: 'pdldata.person.kjoenn',
		navn: 'pdldata.person.navn',
		telefonnummer: {
			pdl: 'pdldata.person.telefonnummer',
		},
		fullmakt: 'fullmakt',
		fullmaktPDL: 'pdldata.person.fullmakt',
		vergemaal: 'pdldata.person.vergemaal',
		sikkerhetstiltak: 'pdldata.person.sikkerhetstiltak',
		tilrettelagtKommunikasjon: 'pdldata.person.tilrettelagtKommunikasjon',
	}

	return {
		alder: {
			label: 'Alder',
			checked: has(paths.alder) || has(paths.foedtEtter) || has(paths.foedtFoer),
			add: () => setMulti([paths.alder, null], [paths.foedtEtter, null], [paths.foedtFoer, null]),
			remove: () => del([paths.alder, paths.foedtEtter, paths.foedtFoer]),
		},
		foedested: {
			label: 'Fødested',
			checked: has(paths.foedested),
			add: () => set(paths.foedested, [getInitialFoedested(initMaster)]),
			remove: () => del([paths.foedested]),
		},
		foedselsdato: {
			label: 'Fødselsdato',
			checked: has(paths.foedselsdato),
			add: () => set(paths.foedselsdato, [getInitialFoedselsdato(initMaster)]),
			remove: () => del([paths.foedselsdato]),
		},
		doedsdato: {
			label: 'Dødsdato',
			checked: has(paths.doedsfall),
			add: () => set(paths.doedsfall, [getInitialDoedsfall(initMaster)]),
			remove: () => del([paths.doedsfall]),
		},
		statsborgerskap: {
			label: 'Statsborgerskap',
			checked: has(paths.statsborgerskap),
			add() {
				_.has(personFoerLeggTil, 'pdlforvalter[0].person.statsborgerskap')
					? set(paths.statsborgerskap, fjernIdFoerLeggTil('statsborgerskap'))
					: set(paths.statsborgerskap, [getInitialStatsborgerskap(initMaster)])
			},
			remove() {
				del([paths.statsborgerskap])
			},
		},
		innvandretFraLand: {
			label: 'Innvandret fra',
			checked: has(paths.innflytting),
			add() {
				_.has(personFoerLeggTil, 'pdlforvalter[0].person.innflytting')
					? set(paths.innflytting, fjernIdFoerLeggTil('innflytting'))
					: set(paths.innflytting, [
							{
								fraflyttingsland: '',
								fraflyttingsstedIUtlandet: '',
								innflyttingsdato: null as unknown as string,
								master: 'FREG',
								kilde: 'Dolly',
							},
						])
			},
			remove() {
				del(paths.innflytting)
			},
		},
		utvandretTilLand: {
			label: 'Utvandret til',
			checked: has(paths.utflytting),
			add() {
				_.has(personFoerLeggTil, 'pdlforvalter[0].person.utflytting')
					? set(paths.utflytting, fjernIdFoerLeggTil('utflytting'))
					: set(paths.utflytting, [
							{
								tilflyttingsland: '',
								tilflyttingsstedIUtlandet: '',
								utflyttingsdato: null as unknown as string,
								master: 'FREG',
								kilde: 'Dolly',
							},
						])
			},
			remove() {
				del(paths.utflytting)
			},
		},
		kjonn: {
			label: 'Kjønn',
			checked: has(paths.kjoenn),
			add: () => set(paths.kjoenn, [getInitialKjoenn(initMaster)]),
			remove: () => del(paths.kjoenn),
		},
		navn: {
			label: 'Navn',
			checked: has(paths.navn),
			add: () => set(paths.navn, [getInitialNavn(initMaster)]),
			remove: () => del(paths.navn),
		},
		telefonnummer: {
			label: 'Telefonnummer',
			checked: has(paths.telefonnummer.pdl),
			add() {
				_.has(personFoerLeggTil, 'pdlforvalter.person.telefonnummer')
					? set(paths.telefonnummer.pdl, fjernIdFoerLeggTil('telefonnummer'))
					: set(paths.telefonnummer.pdl, [
							{
								landskode: '+47',
								nummer: '',
								prioritet: 1,
								kilde: 'Dolly',
								master: 'PDL',
							},
						])
			},
			remove() {
				del(paths.telefonnummer.pdl)
			},
		},
		vergemaal: {
			label: 'Vergemål',
			checked: has(paths.vergemaal),
			add: () => set(paths.vergemaal, [initialVergemaal]),
			remove: () => del(paths.vergemaal),
		},
		fullmakt: {
			label: 'Har fullmakt',
			checked: has(paths.fullmakt) || has(paths.fullmaktPDL),
			add: () => {
				set('fullmakt', [
					{
						...initialFullmakt,
						nyFullmektig: initialPdlPerson,
						master: identMaster === 'PDL' || identtype === 'NPID' ? 'PDL' : 'FREG',
					},
				])
				set('pdldata.person.fullmakt', [{ nyfullMektig: initialPdlPerson }])
			},
			remove: () => {
				del('fullmakt')
				del('pdldata.person.fullmakt')
			},
		},
		sikkerhetstiltak: {
			label: 'Sikkerhetstiltak',
			checked: has(paths.sikkerhetstiltak),
			add: () => set(paths.sikkerhetstiltak, [initialSikkerhetstiltak]),
			remove: () => del(paths.sikkerhetstiltak),
		},
		tilrettelagtKommunikasjon: {
			label: 'Tilrettelagt komm.',
			checked: has(paths.tilrettelagtKommunikasjon),
			add() {
				_.has(personFoerLeggTil, 'pdlforvalter[0].person.tilrettelagtKommunikasjon')
					? set(paths.tilrettelagtKommunikasjon, fjernIdFoerLeggTil('tilrettelagtKommunikasjon'))
					: set(paths.tilrettelagtKommunikasjon, [initialTilrettelagtKommunikasjon])
			},
			remove() {
				del(paths.tilrettelagtKommunikasjon)
			},
		},
	}
}
