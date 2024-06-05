import React, { useContext } from 'react'
import _ from 'lodash'
import Panel from '@/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import {
	getInitialDoedsfall,
	getInitialFoedsel,
	getInitialKjoenn,
	getInitialNavn,
	getInitialStatsborgerskap,
	initialFullmakt,
	initialSikkerhetstiltak,
	initialTilrettelagtKommunikasjon,
	initialVergemaal,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { useGruppeIdenter } from '@/utils/hooks/useGruppe'

const ignoreKeysTestnorge = [
	'alder',
	'innvandretFraLand',
	'utvandretTilLand',
	'identtype',
	'vergemaal',
	'tilrettelagtKommunikasjon',
]

const utvandret = 'utvandretTilLand'

// @ts-ignore
export const PersoninformasjonPanel = ({ stateModifier, testnorgeIdent }) => {
	const sm = stateModifier(PersoninformasjonPanel.initialValues)
	const opts = useContext(BestillingsveilederContext)

	const gruppeId = opts?.gruppeId || opts?.gruppe?.id
	const { identer, loading: gruppeLoading, error: gruppeError } = useGruppeIdenter(gruppeId)
	const harTestnorgeIdenter = identer?.filter((ident) => ident.master === 'PDL').length > 0

	const opprettFraEksisterende = opts.is.opprettFraIdenter
	const leggTil = opts.is.leggTil || opts.is.leggTilPaaGruppe

	const testNorgePerson = opts?.identMaster === 'PDL'
	const npidPerson = opts?.identtype === 'NPID'
	const ukjentGruppe = !gruppeId
	const tekstUkjentGruppe = 'Funksjonen er deaktivert da personer for relasjon er ukjent'
	const testNorgeFlere = testNorgePerson && opts?.antall > 1
	const tekstFlerePersoner = 'Funksjonen er kun tilgjengelig per individ, ikke for gruppe'
	const leggTilPaaGruppe = !!opts?.leggTilPaaGruppe
	const tekstLeggTilPaaGruppe =
		'Støttes ikke for "legg-til-på-alle" i grupper som inneholder personer fra Test-Norge'

	const harFnr = opts.identtype === 'FNR'
	// Noen egenskaper kan ikke endres når personen opprettes fra eksisterende eller videreføres med legg til

	const getIgnoreKeys = () => {
		var ignoreKeys = testnorgeIdent ? [...ignoreKeysTestnorge] : ['identtype']
		if (
			(testnorgeIdent && (ukjentGruppe || opts?.antall > 1)) ||
			(harTestnorgeIdenter && leggTilPaaGruppe)
		) {
			ignoreKeys.push('fullmakt')
			ignoreKeys.push('vergemaal')
			ignoreKeys.push('innvandretFraLand')
			ignoreKeys.push('utvandretTilLand')
		}
		if (sm.attrs.utenlandskBankkonto.checked) {
			ignoreKeys.push('norskBankkonto')
		} else {
			ignoreKeys.push('utenlandskBankkonto')
		}
		if (!testnorgeIdent && !harFnr) {
			ignoreKeys.push(utvandret)
		}
		if (!harFnr) {
			ignoreKeys.push('innvandretFraLand')
			ignoreKeys.push('vergemaal')
		}
		return ignoreKeys
	}

	if (testnorgeIdent) {
		return (
			// @ts-ignore
			<Panel
				heading={PersoninformasjonPanel.heading}
				startOpen
				checkAttributeArray={() => sm.batchAdd(getIgnoreKeys())}
				uncheckAttributeArray={sm.batchRemove}
				iconType={'personinformasjon'}
			>
				<AttributtKategori title="Alder" attr={sm.attrs}>
					<Attributt attr={sm.attrs.foedsel} />
					<Attributt attr={sm.attrs.doedsdato} />
				</AttributtKategori>
				<AttributtKategori title="Nasjonalitet" attr={sm.attrs}>
					<Attributt attr={sm.attrs.statsborgerskap} />
				</AttributtKategori>
				<AttributtKategori title="Diverse" attr={sm.attrs}>
					<Attributt attr={sm.attrs.kjonn} />
					<Attributt attr={sm.attrs.navn} />
					<Attributt attr={sm.attrs.sprakKode} />
					<Attributt attr={sm.attrs.egenAnsattDatoFom} />
					<Attributt
						attr={sm.attrs.norskBankkonto}
						disabled={sm.attrs.utenlandskBankkonto.checked}
					/>
					<Attributt
						attr={sm.attrs.utenlandskBankkonto}
						disabled={sm.attrs.norskBankkonto.checked}
					/>
					<Attributt attr={sm.attrs.telefonnummer} />
					<Attributt
						attr={sm.attrs.fullmakt}
						disabled={ukjentGruppe || testNorgeFlere}
						title={(ukjentGruppe && tekstUkjentGruppe) || (testNorgeFlere && tekstFlerePersoner)}
					/>
					<Attributt attr={sm.attrs.sikkerhetstiltak} />
					<Attributt attr={sm.attrs.tilrettelagtKommunikasjon} />
				</AttributtKategori>
			</Panel>
		)
	}

	return (
		// @ts-ignore
		<Panel
			heading={PersoninformasjonPanel.heading}
			startOpen
			checkAttributeArray={() => sm.batchAdd(getIgnoreKeys())}
			uncheckAttributeArray={sm.batchRemove}
			iconType={'personinformasjon'}
		>
			<AttributtKategori title="Alder" attr={sm.attrs}>
				<Attributt attr={sm.attrs.alder} vis={!opprettFraEksisterende && !leggTil} />
				<Attributt attr={sm.attrs.foedsel} />
				<Attributt attr={sm.attrs.doedsdato} />
			</AttributtKategori>

			<AttributtKategori title="Nasjonalitet" attr={sm.attrs}>
				<Attributt attr={sm.attrs.statsborgerskap} />
				<Attributt
					attr={sm.attrs.innvandretFraLand}
					disabled={!harFnr || (harTestnorgeIdenter && leggTilPaaGruppe)}
					title={
						(!harFnr &&
							'Personer med identtype DNR eller NPID kan ikke innvandre fordi de ikke har norsk statsborgerskap') ||
						(harTestnorgeIdenter && leggTilPaaGruppe && tekstLeggTilPaaGruppe)
					}
				/>
				<Attributt
					attr={sm.attrs.utvandretTilLand}
					disabled={!harFnr || (harTestnorgeIdenter && leggTilPaaGruppe)}
					title={
						(!harFnr &&
							'Personer med identtype DNR eller NPID kan ikke utvandre fordi de ikke har norsk statsborgerskap') ||
						(harTestnorgeIdenter && leggTilPaaGruppe && tekstLeggTilPaaGruppe)
					}
				/>
			</AttributtKategori>
			<AttributtKategori title="Diverse" attr={sm.attrs}>
				<Attributt attr={sm.attrs.kjonn} vis={!opprettFraEksisterende} />
				<Attributt attr={sm.attrs.navn} />
				<Attributt attr={sm.attrs.sprakKode} />
				<Attributt attr={sm.attrs.egenAnsattDatoFom} />
				<Attributt attr={sm.attrs.norskBankkonto} disabled={sm.attrs.utenlandskBankkonto.checked} />
				<Attributt attr={sm.attrs.utenlandskBankkonto} disabled={sm.attrs.norskBankkonto.checked} />
				<Attributt attr={sm.attrs.telefonnummer} />
				<Attributt
					attr={sm.attrs.vergemaal}
					disabled={npidPerson || (harTestnorgeIdenter && leggTilPaaGruppe)}
					title={
						(npidPerson && 'Ikke tilgjengelig for personer med identtype NPID') ||
						(harTestnorgeIdenter && leggTilPaaGruppe && tekstLeggTilPaaGruppe)
					}
				/>
				<Attributt
					attr={sm.attrs.fullmakt}
					disabled={npidPerson || (harTestnorgeIdenter && leggTilPaaGruppe)}
					title={
						(npidPerson && 'Ikke tilgjengelig for personer med identtype NPID') ||
						(harTestnorgeIdenter && leggTilPaaGruppe && tekstLeggTilPaaGruppe)
					}
				/>
				<Attributt attr={sm.attrs.sikkerhetstiltak} />
				<Attributt attr={sm.attrs.tilrettelagtKommunikasjon} />
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
		foedsel: 'pdldata.person.foedsel',
		doedsfall: 'pdldata.person.doedsfall',
		statsborgerskap: 'pdldata.person.statsborgerskap',
		innflytting: 'pdldata.person.innflytting',
		utflytting: 'pdldata.person.utflytting',
		kjoenn: 'pdldata.person.kjoenn',
		navn: 'pdldata.person.navn',
		spraakKode: {
			tpsM: 'tpsMessaging.spraakKode',
		},
		egenAnsattDatoFom: {
			tpsM: 'tpsMessaging.egenAnsattDatoFom',
			skjerming: 'skjerming.egenAnsattDatoFom',
		},
		egenAnsattDatoTom: {
			tpsM: 'tpsMessaging.egenAnsattDatoTom',
			skjerming: 'skjerming.egenAnsattDatoTom',
		},
		skjermetFra: 'skjermingsregister.skjermetFra',
		norskBankkonto: 'bankkonto.norskBankkonto',
		utenlandskBankkonto: 'bankkonto.utenlandskBankkonto',
		telefonnummer: {
			pdl: 'pdldata.person.telefonnummer',
			tpsM: 'tpsMessaging.telefonnummer',
		},
		vergemaal: 'pdldata.person.vergemaal',
		fullmakt: 'pdldata.person.fullmakt',
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
		foedsel: {
			label: 'Fødsel',
			checked: has(paths.foedsel),
			add: () => set(paths.foedsel, [getInitialFoedsel(initMaster)]),
			remove: () => del([paths.foedsel]),
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
		sprakKode: {
			label: 'Språk',
			checked: has(paths.spraakKode.tpsM),
			add: () => set(paths.spraakKode.tpsM, ''),
			remove: () => del(paths.spraakKode.tpsM),
		},
		egenAnsattDatoFom: {
			label: 'Skjerming',
			checked: has(paths.egenAnsattDatoFom.tpsM) || has(paths.egenAnsattDatoFom.skjerming),
			add() {
				setMulti(
					[
						paths.egenAnsattDatoFom.skjerming,
						_.get(personFoerLeggTil, paths.skjermetFra)?.substring(0, 10) ||
							_.get(personFoerLeggTil, paths.egenAnsattDatoFom.tpsM) ||
							new Date(),
					],
					[paths.egenAnsattDatoTom.skjerming, undefined],
				)
			},
			remove() {
				del('skjerming')
			},
		},
		norskBankkonto: {
			label: 'Norsk bank',
			checked: has(paths.norskBankkonto),
			add: () =>
				set(paths.norskBankkonto, {
					kontonummer: '',
					tilfeldigKontonummer: opts.antall && opts.antall > 1,
				}),
			remove: () => del(paths.norskBankkonto),
		},
		utenlandskBankkonto: {
			label: 'Utenlandsk bank',
			checked: has(paths.utenlandskBankkonto),
			add: () =>
				set(paths.utenlandskBankkonto, {
					kontonummer: '',
					tilfeldigKontonummer: false,
					swift: 'BANKXX11222',
					landkode: null,
					banknavn: '',
					iban: '',
					valuta: null,
					bankAdresse1: '',
					bankAdresse2: '',
					bankAdresse3: '',
				}),
			remove: () => del(paths.utenlandskBankkonto),
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
			label: 'Fullmakt',
			checked: has(paths.fullmakt),
			add: () => set(paths.fullmakt, [initialFullmakt]),
			remove: () => del(paths.fullmakt),
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
