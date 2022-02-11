import React, { useContext } from 'react'
import _get from 'lodash/get'
import _has from 'lodash/has'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import Formatters from '~/utils/DataFormatter'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import {
	initialDoedsfall,
	initialFoedsel,
	initialKjoenn,
	initialNavn,
	initialPdlPerson,
	initialStatsborgerskap,
	initialTilrettelagtKommunikasjon,
	initialVergemaal,
} from '~/components/fagsystem/pdlf/form/initialValues'

// @ts-ignore
export const PersoninformasjonPanel = ({ stateModifier }) => {
	const sm = stateModifier(PersoninformasjonPanel.initialValues)
	const opts = useContext(BestillingsveilederContext)
	const opprettFraEksisterende = opts.is.opprettFraIdenter
	const leggTil = opts.is.leggTil

	const harFnr = opts.identtype === 'FNR'
	//Noen egenskaper kan ikke endres når personen opprettes fra eksisterende eller videreføres med legg til

	return (
		// @ts-ignore
		<Panel
			heading={PersoninformasjonPanel.heading}
			startOpen
			checkAttributeArray={() => sm.batchAdd('identtype')}
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
						'Personer med identtype DNR eller BOST kan ikke utvandre fordi de ikke har norsk statsborgerskap'
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

	return {
		alder: {
			label: 'Alder',
			checked:
				has('pdldata.opprettNyPerson.alder') ||
				has('pdldata.opprettNyPerson.foedtEtter') ||
				has('pdldata.opprettNyPerson.foedtFoer'),
			add: () =>
				setMulti(
					['pdldata.opprettNyPerson.alder', Formatters.randomIntInRange(30, 60)],
					['pdldata.opprettNyPerson.foedtEtter', null],
					['pdldata.opprettNyPerson.foedtFoer', null]
				),
			remove: () =>
				del([
					'pdldata.opprettNyPerson.alder',
					'pdldata.opprettNyPerson.foedtEtter',
					'pdldata.opprettNyPerson.foedtFoer',
				]),
		},
		foedsel: {
			label: 'Fødsel',
			checked: has('pdldata.person.foedsel'),
			add: () => set('pdldata.person.foedsel', [initialFoedsel]),
			remove: () => del(['pdldata.person.foedsel']),
		},
		doedsdato: {
			label: 'Dødsdato',
			checked: has('pdldata.person.doedsfall'),
			add: () => set('pdldata.person.doedsfall', [initialDoedsfall]),
			remove: () => del(['pdldata.person.doedsfall']),
		},
		statsborgerskap: {
			label: 'Statsborgerskap',
			checked: has('pdldata.person.statsborgerskap'),
			add() {
				_has(personFoerLeggTil, 'pdlforvalter[0].person.statsborgerskap')
					? set('pdldata.person.statsborgerskap', fjernIdFoerLeggTil('statsborgerskap'))
					: set('pdldata.person.statsborgerskap', [initialStatsborgerskap])
			},
			remove() {
				del(['pdldata.person.statsborgerskap'])
			},
		},
		innvandretFraLand: {
			label: 'Innvandret fra',
			checked: has('pdldata.person.innflytting'),
			add() {
				_has(personFoerLeggTil, 'pdlforvalter[0].person.innflytting')
					? set('pdldata.person.innflytting', fjernIdFoerLeggTil('innflytting'))
					: set('pdldata.person.innflytting', [
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
				del('pdldata.person.innflytting')
			},
		},
		utvandretTilLand: {
			label: 'Utvandret til',
			checked: has('pdldata.person.utflytting'),
			add() {
				_has(personFoerLeggTil, 'pdlforvalter[0].person.utflytting')
					? set('pdldata.person.utflytting', fjernIdFoerLeggTil('utflytting'))
					: set('pdldata.person.utflytting', [
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
				del('pdldata.person.utflytting')
			},
		},
		kjonn: {
			label: 'Kjønn',
			checked: has('pdldata.person.kjoenn'),
			add: () => set('pdldata.person.kjoenn', [initialKjoenn]),
			remove: () => del('pdldata.person.kjoenn'),
		},
		navn: {
			label: 'Navn',
			checked: has('pdldata.person.navn'),
			add: () => set('pdldata.person.navn', [initialNavn]),
			remove: () => del('pdldata.person.navn'),
		},
		sprakKode: {
			label: 'Språk',
			checked: has('tpsf.sprakKode') || has('tpsMessaging.spraakKode'),
			add: () => set('tpsMessaging.spraakKode', ''),
			remove: () => del(['tpsMessaging.spraakKode', 'tpsf.spraakKode']),
		},
		egenAnsattDatoFom: {
			label: 'Skjerming',
			checked: has('tpsf.egenAnsattDatoFom') || has('tpsMessaging.egenAnsattDatoFom'),
			add() {
				setMulti(
					[
						'tpsMessaging.egenAnsattDatoFom',
						_get(personFoerLeggTil, 'skjermingsregister.skjermetFra')?.substring(0, 10) ||
							_get(personFoerLeggTil, 'tpsMessaging.egenAnsattDatoFom') ||
							new Date(),
					],
					['tpsMessaging.egenAnsattDatoTom', undefined],
					[
						'skjerming.egenAnsattDatoFom',
						_get(personFoerLeggTil, 'skjermingsregister.skjermetFra')?.substring(0, 10) ||
							_get(personFoerLeggTil, 'tpsMessaging.egenAnsattDatoFom') ||
							new Date(),
					],
					['skjerming.egenAnsattDatoTom', undefined]
				)
			},
			remove() {
				del([
					'tpsMessaging.egenAnsattDatoFom',
					'tpsMessaging.egenAnsattDatoTom',
					'tpsf.egenAnsattDatoFom',
					'tpsf.egenAnsattDatoTom',
					'skjerming',
				])
			},
		},
		telefonnummer: {
			label: 'Telefonnummer',
			checked: has('pdldata.person.telefonnummer'),
			add() {
				_has(personFoerLeggTil, 'pdlforvalter[0].person.telefonnummer')
					? setMulti(
							['pdldata.person.telefonnummer', fjernIdFoerLeggTil('telefonnummer')],
							['tpsMessaging.telefonnummer', _get(personFoerLeggTil, 'tpsMessaging.telefonnumre')]
					  )
					: setMulti(
							[
								'pdldata.person.telefonnummer',
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
								'tpsMessaging.telefonnummer',
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
				del(['pdldata.person.telefonnummer', 'tpsMessaging.telefonnummer'])
			},
		},
		vergemaal: {
			label: 'Vergemål',
			checked: has('pdldata.person.vergemaal'),
			add: () => set('pdldata.person.vergemaal', [initialVergemaal]),
			remove: () => del('pdldata.person.vergemaal'),
		},
		fullmakt: {
			label: 'Fullmakt',
			checked: has('pdldata.person.fullmakt'),
			add: () =>
				set('pdldata.person.fullmakt', [
					{
						omraader: [],
						gyldigFraOgMed: null,
						gyldigTilOgMed: null,
						nyFullmektig: initialPdlPerson,
						kilde: 'Dolly',
						master: 'PDL',
					},
				]),
			remove: () => del('pdldata.person.fullmakt'),
		},
		sikkerhetstiltak: {
			label: 'Sikkerhetstiltak',
			checked: has('pdldata.person.sikkerhetstiltak'),
			add: () =>
				setMulti(
					[
						'pdldata.person.sikkerhetstiltak',
						[
							{
								tiltakstype: '',
								beskrivelse: '',
								kontaktperson: {
									personident: '',
									enhet: '',
								},
								gyldigFraOgMed: new Date(),
								gyldigTilOgMed: null,
								kilde: 'Dolly',
								master: 'PDL',
							},
						],
					],
					[
						'tpsMessaging.sikkerhetstiltak',
						[
							{
								tiltakstype: '',
								beskrivelse: '',
								gyldigFraOgMed: new Date(),
								gyldigTilOgMed: null,
							},
						],
					]
				),
			remove: () => del(['pdldata.person.sikkerhetstiltak', 'tpsMessaging.sikkerhetstiltak']),
		},
		tilrettelagtKommunikasjon: {
			label: 'Tilrettelagt komm.',
			checked: has('pdldata.person.tilrettelagtKommunikasjon'),
			add() {
				_has(personFoerLeggTil, 'pdlforvalter[0].person.tilrettelagtKommunikasjon')
					? set(
							'pdldata.person.tilrettelagtKommunikasjon',
							fjernIdFoerLeggTil('tilrettelagtKommunikasjon')
					  )
					: set('pdldata.person.tilrettelagtKommunikasjon', [initialTilrettelagtKommunikasjon])
			},
			remove() {
				del('pdldata.person.tilrettelagtKommunikasjon')
			},
		},
		utenlandskBankkonto: {
			label: 'Utenlandsk bank',
			checked: has('tpsMessaging.utenlandskBankkonto'),
			add: () =>
				set('tpsMessaging.utenlandskBankkonto', {
					kontonummer: '',
					swift: '',
					landkode: null,
					banknavn: '',
					iban: '',
					valuta: null,
					bankAdresse1: '',
					bankAdresse2: '',
					bankAdresse3: '',
				}),
			remove: () => del('tpsMessaging.utenlandskBankkonto'),
		},
		norskBankkonto: {
			label: 'Norsk bank',
			checked: has('tpsMessaging.norskBankkonto'),
			add: () =>
				set('tpsMessaging.norskBankkonto', {
					kontonummer: '',
				}),
			remove: () => del('tpsMessaging.norskBankkonto'),
		},
	}
}
