import React, { useContext } from 'react'
import _get from 'lodash/get'
import _has from 'lodash/has'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import Formatters from '~/utils/DataFormatter'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import { initialPdlPerson } from '~/components/fagsystem/pdlf/form/initialValues'
import { addDays, subDays } from 'date-fns'
import { cloneDeep } from 'lodash'

const innvandret = (personFoerLeggTil) =>
	_get(personFoerLeggTil, 'tpsf.innvandretUtvandret[0].innutvandret') === 'INNVANDRET'

export const PersoninformasjonPanel = ({ stateModifier }) => {
	const sm = stateModifier(PersoninformasjonPanel.initialValues)
	const opts = useContext(BestillingsveilederContext)
	const opprettFraEksisterende = opts.is.opprettFraIdenter
	const leggTil = opts.is.leggTil
	const { personFoerLeggTil } = opts

	const tomInnvandretUtvandret =
		personFoerLeggTil && _get(personFoerLeggTil, 'tpsf.innvandretUtvandret').length < 1

	const harFnr = opts.identtype === 'FNR'
	const harFnrLeggTil = _get(personFoerLeggTil, 'tpsf.identtype') === 'FNR'
	//Noen egenskaper kan ikke endres når personen opprettes fra eksisterende eller videreføres med legg til

	const utvandretTitle = () => {
		if (!harFnr) {
			return 'Personer med identtype DNR eller BOST kan ikke utvandre fordi de ikke har norsk statsborgerskap'
		} else if (leggTil && !tomInnvandretUtvandret && !innvandret(personFoerLeggTil)) {
			return 'Personen må innvandre før den kan utvandre igjen'
		} else return null
	}

	return (
		<Panel
			heading={PersoninformasjonPanel.heading}
			startOpen
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType={'personinformasjon'}
		>
			<AttributtKategori title="Alder">
				<Attributt attr={sm.attrs.alder} vis={!opprettFraEksisterende && !leggTil} />
				<Attributt attr={sm.attrs.doedsdato} />
			</AttributtKategori>

			<AttributtKategori title="Nasjonalitet">
				<Attributt attr={sm.attrs.statsborgerskap} />
				<Attributt
					attr={sm.attrs.innvandretFraLand}
					disabled={
						(tomInnvandretUtvandret && harFnrLeggTil) ||
						(!tomInnvandretUtvandret && innvandret(personFoerLeggTil))
					}
					title={
						(tomInnvandretUtvandret && harFnrLeggTil) ||
						(!tomInnvandretUtvandret && innvandret(personFoerLeggTil))
							? 'Personen må utvandre før den kan innvandre igjen'
							: null
					}
				/>
				<Attributt
					attr={sm.attrs.utvandretTilLand}
					disabled={
						!harFnr || (leggTil && !tomInnvandretUtvandret && !innvandret(personFoerLeggTil))
					}
					title={utvandretTitle()}
				/>
			</AttributtKategori>
			<AttributtKategori title="Diverse">
				<Attributt attr={sm.attrs.identtype} vis={leggTil} />
				<Attributt attr={sm.attrs.identHistorikk} />
				<Attributt attr={sm.attrs.kjonn} vis={!opprettFraEksisterende} />
				<Attributt attr={sm.attrs.harMellomnavn} />
				<Attributt attr={sm.attrs.harNyttNavn} vis={leggTil} />
				<Attributt attr={sm.attrs.sprakKode} />
				<Attributt attr={sm.attrs.egenAnsattDatoFom} />
				<Attributt attr={sm.attrs.erForsvunnet} />
				<Attributt attr={sm.attrs.norskBankkonto} disabled={sm.attrs.utenlandskBankkonto.checked} />
				<Attributt attr={sm.attrs.utenlandskBankkonto} disabled={sm.attrs.norskBankkonto.checked} />
				<Attributt attr={sm.attrs.spesreg} />
				<Attributt attr={sm.attrs.telefonnummer} />
				<Attributt attr={sm.attrs.vergemaal} />
				<Attributt attr={sm.attrs.fullmakt} />
				<Attributt attr={sm.attrs.sikkerhetstiltak} />
			</AttributtKategori>
		</Panel>
	)
}

PersoninformasjonPanel.heading = 'Personinformasjon'

PersoninformasjonPanel.initialValues = ({ set, setMulti, del, has, opts }) => {
	const { personFoerLeggTil } = opts

	const telefonnummerFoerLeggTil = () => {
		const tlfListe = _get(personFoerLeggTil, 'pdlforvalter[0].person.telefonnummer')
		const tlfListeClone = cloneDeep(tlfListe)
		tlfListeClone.forEach((nr) => {
			if (_has(nr, 'id')) {
				delete nr.id
			}
		})
		return tlfListeClone
	}

	return {
		alder: {
			label: 'Alder',
			checked: has('tpsf.alder') || has('tpsf.foedtEtter') || has('tpsf.foedtFoer'),
			add: () => set('tpsf.alder', Formatters.randomIntInRange(30, 60)),
			remove: () => del(['tpsf.alder', 'tpsf.foedtEtter', 'tpsf.foedtFoer']),
		},
		doedsdato: {
			label: 'Dødsdato',
			checked: has('tpsf.doedsdato'),
			add: () => set('tpsf.doedsdato', null),
			remove: () => del(['tpsf.doedsdato', 'tpsf.alder', 'tpsf.foedtEtter', 'tpsf.foedtFoer']),
		},
		statsborgerskap: {
			label: 'Statsborgerskap',
			checked: has('tpsf.statsborgerskap'),
			add() {
				setMulti(
					['tpsf.statsborgerskap', ''],
					['tpsf.statsborgerskapRegdato', null],
					['tpsf.statsborgerskapTildato', null]
				)
			},
			remove() {
				del(
					['tpsf.statsborgerskap', 'tpsf.statsborgerskapRegdato'],
					['tpsf.statsborgerskap', 'tpsf.statsborgerskapTildato']
				)
			},
		},
		innvandretFraLand: {
			label: 'Innvandret fra',
			checked: has('tpsf.innvandretFraLand'),
			add() {
				setMulti(['tpsf.innvandretFraLand', ''], ['tpsf.innvandretFraLandFlyttedato', null])
			},
			remove() {
				del(['tpsf.innvandretFraLand', 'tpsf.innvandretFraLandFlyttedato'])
			},
		},
		utvandretTilLand: {
			label: 'Utvandret til',
			checked: has('tpsf.utvandretTilLand'),
			add() {
				setMulti(['tpsf.utvandretTilLand', ''], ['tpsf.utvandretTilLandFlyttedato', null])
			},
			remove() {
				del(['tpsf.utvandretTilLand', 'tpsf.utvandretTilLandFlyttedato'])
			},
		},
		identHistorikk: {
			label: 'Identhistorikk',
			checked: has('tpsf.identHistorikk'),
			add: () =>
				set('tpsf.identHistorikk', [
					{
						foedtEtter: null,
						foedtFoer: null,
						identtype: null,
						kjonn: null,
						regdato: null,
					},
				]),
			remove: () => del('tpsf.identHistorikk'),
		},
		kjonn: {
			label: 'Kjønn',
			checked: has('tpsf.kjonn'),
			add: () =>
				personFoerLeggTil
					? setMulti(['tpsf.kjonn', ''], ['tpsf.identtype', 'FNR'])
					: set('tpsf.kjonn', ''),
			remove: () => del(['tpsf.kjonn', 'tpsf.identtype']),
		},
		harMellomnavn: {
			label: 'Har mellomnavn',
			checked: has('tpsf.harMellomnavn'),
			add: () => set('tpsf.harMellomnavn', true),
			remove: () => del('tpsf.harMellomnavn'),
		},
		harNyttNavn: {
			label: 'Nytt navn',
			checked: has('tpsf.harNyttNavn'),
			add: () => set('tpsf.harNyttNavn', true),
			remove: () => del('tpsf.harNyttNavn'),
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
						_get(personFoerLeggTil, 'tpsf.egenAnsattDatoFom') ||
							_get(personFoerLeggTil, 'tpsMessaging.egenAnsattDatoFom') ||
							new Date(),
					],
					['tpsMessaging.egenAnsattDatoTom', undefined]
				)
			},
			remove() {
				del([
					'tpsMessaging.egenAnsattDatoFom',
					'tpsMessaging.egenAnsattDatoTom',
					'tpsf.egenAnsattDatoFom',
					'tpsf.egenAnsattDatoTom',
				])
			},
		},
		erForsvunnet: {
			label: 'Forsvunnet',
			checked: has('tpsf.erForsvunnet'),
			add() {
				setMulti(['tpsf.erForsvunnet', true], ['tpsf.forsvunnetDato', null])
			},
			remove() {
				del(['tpsf.erForsvunnet', 'tpsf.forsvunnetDato'])
			},
		},
		telefonnummer: {
			label: 'Telefonnummer',
			checked: has('pdldata.person.telefonnummer'),
			add() {
				_has(personFoerLeggTil, 'pdlforvalter[0].person.telefonnummer')
					? //TODO: Før legg til må settes i TpsMessaging også når backend er klar.
					  set('pdldata.person.telefonnummer', telefonnummerFoerLeggTil())
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
										gjeldende: true,
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
		spesreg: {
			label: 'Diskresjonskode',
			checked: has('tpsf.spesreg') || has('tpsf.utenFastBopel'),
			add() {
				setMulti(['tpsf.spesreg', ''], ['tpsf.utenFastBopel', false])
			},
			remove() {
				del(['tpsf.spesreg', 'tpsf.utenFastBopel'])
			},
		},
		identtype: {
			label: 'Identtype',
			checked: has('tpsf.identtype'),
			add: () =>
				setMulti(
					['tpsf.identtype', 'FNR'],
					personFoerLeggTil?.tpsf?.foedselsdato && [
						'tpsf.foedtFoer',
						addDays(new Date(personFoerLeggTil.tpsf.foedselsdato), 14),
					],
					personFoerLeggTil?.tpsf?.foedselsdato && [
						'tpsf.foedtEtter',
						subDays(new Date(personFoerLeggTil.tpsf.foedselsdato), 14),
					]
				),
			remove: () => del(['tpsf.identtype', 'tpsf.foedtEtter', 'tpsf.foedtFoer']),
		},
		vergemaal: {
			label: 'Vergemål',
			checked: has('tpsf.vergemaal'),
			add: () =>
				set('tpsf.vergemaal', {
					embete: null,
					sakType: null,
					mandatType: null,
					vedtakDato: null,
					identType: null,
					harMellomnavn: null,
				}),
			remove: () => del('tpsf.vergemaal'),
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
						gjeldende: true,
					},
				]),
			remove: () => del('pdldata.person.fullmakt'),
		},
		sikkerhetstiltak: {
			label: 'Sikkerhetstiltak',
			checked: has('tpsf.typeSikkerhetTiltak'),
			add: () =>
				setMulti(
					['tpsf.typeSikkerhetTiltak', ''],
					['tpsf.beskrSikkerhetTiltak', ''],
					['tpsf.sikkerhetTiltakDatoFom', new Date()],
					['tpsf.sikkerhetTiltakDatoTom', '']
				),
			remove: () =>
				del([
					'tpsf.typeSikkerhetTiltak',
					'tpsf.beskrSikkerhetTiltak',
					'tpsf.sikkerhetTiltakDatoFom',
					'tpsf.sikkerhetTiltakDatoTom',
				]),
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
