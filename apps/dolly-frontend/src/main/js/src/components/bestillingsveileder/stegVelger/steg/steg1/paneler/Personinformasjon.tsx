import React, { useContext } from 'react'
import _get from 'lodash/get'
import _has from 'lodash/has'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import {
	initialDoedsfall,
	initialFoedsel,
	initialFullmakt,
	initialKjoenn,
	initialNavn,
	initialSikkerhetstiltak,
	initialStatsborgerskap,
	initialTilrettelagtKommunikasjon,
	initialTpsSikkerhetstiltak,
	initialVergemaal,
} from '~/components/fagsystem/pdlf/form/initialValues'

const ignoreKeysTestnorge = [
	'alder',
	'foedsel',
	'doedsdato',
	'statsborgerskap',
	'innvandretFraLand',
	'utvandretTilLand',
	'identtype',
	'kjonn',
	'navn',
	'telefonnummer',
	'vergemaal',
	'fullmakt',
	'sikkerhetstiltak',
	'tilrettelagtKommunikasjon',
]

// @ts-ignore
export const PersoninformasjonPanel = ({ stateModifier, testnorgeIdent }) => {
	const sm = stateModifier(PersoninformasjonPanel.initialValues)
	const opts = useContext(BestillingsveilederContext)
	const opprettFraEksisterende = opts.is.opprettFraIdenter
	const leggTil = opts.is.leggTil

	const harFnr = opts.identtype === 'FNR'
	//Noen egenskaper kan ikke endres når personen opprettes fra eksisterende eller videreføres med legg til

	const getIgnoreKeys = () => {
		const ignoreKeys = testnorgeIdent ? [...ignoreKeysTestnorge] : ['identtype']
		if (sm.attrs.utenlandskBankkonto.checked) {
			ignoreKeys.push('norskBankkonto')
		} else {
			ignoreKeys.push('utenlandskBankkonto')
		}
		if (!testnorgeIdent && !harFnr) {
			ignoreKeys.push('utvandretTilLand')
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
				<AttributtKategori title="Diverse">
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
			<AttributtKategori title="Alder">
				<Attributt attr={sm.attrs.alder} vis={!opprettFraEksisterende && !leggTil} />
				<Attributt attr={sm.attrs.foedsel} />
				<Attributt attr={sm.attrs.doedsdato} />
			</AttributtKategori>

			<AttributtKategori title="Nasjonalitet">
				<Attributt attr={sm.attrs.statsborgerskap} />
				<Attributt attr={sm.attrs.innvandretFraLand} />
				<Attributt
					attr={sm.attrs.utvandretTilLand}
					disabled={!harFnr}
					title={
						!harFnr
							? 'Personer med identtype DNR eller NPID kan ikke utvandre fordi de ikke har norsk statsborgerskap'
							: ''
					}
				/>
			</AttributtKategori>
			<AttributtKategori title="Diverse">
				<Attributt attr={sm.attrs.kjonn} vis={!opprettFraEksisterende} />
				<Attributt attr={sm.attrs.navn} />
				<Attributt attr={sm.attrs.sprakKode} />
				<Attributt attr={sm.attrs.egenAnsattDatoFom} />
				<Attributt attr={sm.attrs.norskBankkonto} disabled={sm.attrs.utenlandskBankkonto.checked} />
				<Attributt attr={sm.attrs.utenlandskBankkonto} disabled={sm.attrs.norskBankkonto.checked} />
				<Attributt attr={sm.attrs.telefonnummer} />
				<Attributt attr={sm.attrs.vergemaal} />
				<Attributt attr={sm.attrs.fullmakt} />
				<Attributt attr={sm.attrs.sikkerhetstiltak} />
				<Attributt attr={sm.attrs.tilrettelagtKommunikasjon} />
			</AttributtKategori>
		</Panel>
	)
}

PersoninformasjonPanel.heading = 'Personinformasjon'

// @ts-ignore
PersoninformasjonPanel.initialValues = ({ set, setMulti, del, has, opts }) => {
	const { personFoerLeggTil } = opts

	const fjernIdFoerLeggTil = (path: string) => {
		const pdlDataElement = _get(personFoerLeggTil, `pdlforvalter[0].person.${path}`)
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
			tpsf: 'tpsf.spraakKode',
			tpsM: 'tpsMessaging.spraakKode',
		},
		egenAnsattDatoFom: {
			tpsf: 'tpsf.egenAnsattDatoFom',
			tpsM: 'tpsMessaging.egenAnsattDatoFom',
			skjerming: 'skjerming.egenAnsattDatoFom',
		},
		egenAnsattDatoTom: {
			tpsf: 'tpsf.egenAnsattDatoTom',
			tpsM: 'tpsMessaging.egenAnsattDatoTom',
			skjerming: 'skjerming.egenAnsattDatoTom',
		},
		skjermetFra: 'skjermingsregister.skjermetFra',
		telefonnummer: {
			pdl: 'pdldata.person.telefonnummer',
			tpsM: 'tpsMessaging.telefonnummer',
		},
		vergemaal: 'pdldata.person.vergemaal',
		fullmakt: 'pdldata.person.fullmakt',
		sikkerhetstiltak: {
			pdl: 'pdldata.person.sikkerhetstiltak',
			tpsM: 'tpsMessaging.sikkerhetstiltak',
		},
		tilrettelagtKommunikasjon: 'pdldata.person.tilrettelagtKommunikasjon',
		utenlandskBankkonto: 'bankkonto.utenlandskBankkonto',
		norskBankkonto: 'bankkonto.norskBankkonto',
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
			add: () => set(paths.foedsel, [initialFoedsel]),
			remove: () => del([paths.foedsel]),
		},
		doedsdato: {
			label: 'Dødsdato',
			checked: has(paths.doedsfall),
			add: () => set(paths.doedsfall, [initialDoedsfall]),
			remove: () => del([paths.doedsfall]),
		},
		statsborgerskap: {
			label: 'Statsborgerskap',
			checked: has(paths.statsborgerskap),
			add() {
				_has(personFoerLeggTil, 'pdlforvalter[0].person.statsborgerskap')
					? set(paths.statsborgerskap, fjernIdFoerLeggTil('statsborgerskap'))
					: set(paths.statsborgerskap, [initialStatsborgerskap])
			},
			remove() {
				del([paths.statsborgerskap])
			},
		},
		innvandretFraLand: {
			label: 'Innvandret fra',
			checked: has(paths.innflytting),
			add() {
				_has(personFoerLeggTil, 'pdlforvalter[0].person.innflytting')
					? set(paths.innflytting, fjernIdFoerLeggTil('innflytting'))
					: set(paths.innflytting, [
							{
								fraflyttingsland: '',
								fraflyttingsstedIUtlandet: '',
								innflyttingsdato: new Date(),
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
				_has(personFoerLeggTil, 'pdlforvalter[0].person.utflytting')
					? set(paths.utflytting, fjernIdFoerLeggTil('utflytting'))
					: set(paths.utflytting, [
							{
								tilflyttingsland: '',
								tilflyttingsstedIUtlandet: '',
								utflyttingsdato: new Date(),
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
			add: () => set(paths.kjoenn, [initialKjoenn]),
			remove: () => del(paths.kjoenn),
		},
		navn: {
			label: 'Navn',
			checked: has(paths.navn),
			add: () => set(paths.navn, [initialNavn]),
			remove: () => del(paths.navn),
		},
		sprakKode: {
			label: 'Språk',
			checked: has(paths.spraakKode.tpsf) || has(paths.spraakKode.tpsM),
			add: () => set(paths.spraakKode.tpsM, ''),
			remove: () => del([paths.spraakKode.tpsM, paths.spraakKode.tpsf]),
		},
		egenAnsattDatoFom: {
			label: 'Skjerming',
			checked: has(paths.egenAnsattDatoFom.tpsf) || has(paths.egenAnsattDatoFom.tpsM),
			add() {
				setMulti(
					[
						paths.egenAnsattDatoFom.tpsM,
						_get(personFoerLeggTil, paths.skjermetFra)?.substring(0, 10) ||
							_get(personFoerLeggTil, paths.egenAnsattDatoFom.tpsM) ||
							new Date(),
					],
					[paths.egenAnsattDatoTom.tpsM, undefined],
					[
						paths.egenAnsattDatoFom.skjerming,
						_get(personFoerLeggTil, paths.skjermetFra)?.substring(0, 10) ||
							_get(personFoerLeggTil, paths.egenAnsattDatoFom.tpsM) ||
							new Date(),
					],
					[paths.egenAnsattDatoTom.skjerming, undefined]
				)
			},
			remove() {
				del([
					paths.egenAnsattDatoFom.tpsM,
					paths.egenAnsattDatoTom.tpsM,
					paths.egenAnsattDatoFom.tpsf,
					paths.egenAnsattDatoTom.tpsf,
					'skjerming',
				])
			},
		},
		telefonnummer: {
			label: 'Telefonnummer',
			checked: has(paths.telefonnummer.pdl),
			add() {
				_has(personFoerLeggTil, 'pdlforvalter[0].person.telefonnummer')
					? setMulti(
							[paths.telefonnummer.pdl, fjernIdFoerLeggTil('telefonnummer')],
							[paths.telefonnummer.tpsM, _get(personFoerLeggTil, 'tpsMessaging.telefonnumre')]
					  )
					: setMulti(
							[
								paths.telefonnummer.pdl,
								[
									{
										landskode: '',
										nummer: '',
										prioritet: 1,
										kilde: 'Dolly',
										master: 'PDL',
									},
								],
							],
							[
								paths.telefonnummer.tpsM,
								[
									{
										telefonnummer: '',
										landkode: '',
										telefontype: 'MOBI',
									},
								],
							]
					  )
			},
			remove() {
				del([paths.telefonnummer.pdl, paths.telefonnummer.tpsM])
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
			checked: has(paths.sikkerhetstiltak.pdl),
			add: () =>
				setMulti(
					[paths.sikkerhetstiltak.pdl, [initialSikkerhetstiltak]],
					[paths.sikkerhetstiltak.tpsM, [initialTpsSikkerhetstiltak]]
				),
			remove: () => del([paths.sikkerhetstiltak.pdl, paths.sikkerhetstiltak.tpsM]),
		},
		tilrettelagtKommunikasjon: {
			label: 'Tilrettelagt komm.',
			checked: has(paths.tilrettelagtKommunikasjon),
			add() {
				_has(personFoerLeggTil, 'pdlforvalter[0].person.tilrettelagtKommunikasjon')
					? set(paths.tilrettelagtKommunikasjon, fjernIdFoerLeggTil('tilrettelagtKommunikasjon'))
					: set(paths.tilrettelagtKommunikasjon, [initialTilrettelagtKommunikasjon])
			},
			remove() {
				del(paths.tilrettelagtKommunikasjon)
			},
		},
		utenlandskBankkonto: {
			label: 'Utenlandsk bank',
			checked: has(paths.utenlandskBankkonto),
			add: () =>
				set(paths.utenlandskBankkonto, {
					kontonummer: '',
					tilfeldigKontonummer: false,
					swift: '',
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
		norskBankkonto: {
			label: 'Norsk bank',
			checked: has(paths.norskBankkonto),
			add: () =>
				set(paths.norskBankkonto, {
					kontonummer: '',
				}),
			remove: () => del(paths.norskBankkonto),
		},
	}
}
