import React, { useContext } from 'react'
import _get from 'lodash/get'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import Formatters from '~/utils/DataFormatter'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'

const innvandret = personFoerLeggTil =>
	_get(personFoerLeggTil, 'tpsf.innvandretUtvandret[0].innutvandret') === 'INNVANDRET'

export const PersoninformasjonPanel = ({ stateModifier }) => {
	const sm = stateModifier(PersoninformasjonPanel.initialValues)
	const opts = useContext(BestillingsveilederContext)
	const opprettFraEksisterende = opts.is.opprettFraIdenter
	const leggTil = opts.is.leggTil
	const { personFoerLeggTil } = opts
	//Noen egenskaper kan ikke endres når personen opprettes fra eksisterende eller videreføres med legg til

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
					disabled={innvandret(personFoerLeggTil)}
					title={
						innvandret(personFoerLeggTil)
							? 'Personen må utvandre før den kan innvandre igjen'
							: null
					}
				/>
				<Attributt
					attr={sm.attrs.utvandretTilLand}
					disabled={leggTil && !innvandret(personFoerLeggTil)}
					title={
						leggTil && !innvandret(personFoerLeggTil)
							? 'Personen må innvandre før den kan utvandre igjen'
							: null
					}
				/>
			</AttributtKategori>
			<AttributtKategori title="Diverse">
				<Attributt attr={sm.attrs.identtype} vis={leggTil} />
				<Attributt attr={sm.attrs.identHistorikk} />
				<Attributt attr={sm.attrs.kjonn} vis={!opprettFraEksisterende && !leggTil} />
				<Attributt attr={sm.attrs.harMellomnavn} />
				<Attributt attr={sm.attrs.sprakKode} />
				<Attributt attr={sm.attrs.egenAnsattDatoFom} />
				<Attributt attr={sm.attrs.erForsvunnet} />
				<Attributt attr={sm.attrs.harBankkontonr} />
				<Attributt attr={sm.attrs.telefonnummer_1} />
				<Attributt attr={sm.attrs.spesreg} />
				<Attributt attr={sm.attrs.vergemaal} />
				<Attributt attr={sm.attrs.fullmakt} />
			</AttributtKategori>
		</Panel>
	)
}

PersoninformasjonPanel.heading = 'Personinformasjon'

PersoninformasjonPanel.initialValues = ({ set, setMulti, del, has, opts }) => {
	const { personFoerLeggTil } = opts

	return {
		alder: {
			label: 'Alder',
			checked: has('tpsf.alder') || has('tpsf.foedtEtter') || has('tpsf.foedtFoer'),
			add: () => set('tpsf.alder', Formatters.randomIntInRange(30, 60)),
			remove: () => del(['tpsf.alder', 'tpsf.foedtEtter', 'tpsf.foedtFoer'])
		},
		doedsdato: {
			label: 'Dødsdato',
			checked: has('tpsf.doedsdato'),
			add: () => set('tpsf.doedsdato', null),
			remove: () => del('tpsf.doedsdato')
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
			}
		},
		innvandretFraLand: {
			label: 'Innvandret fra',
			checked: has('tpsf.innvandretFraLand'),
			add() {
				setMulti(['tpsf.innvandretFraLand', ''], ['tpsf.innvandretFraLandFlyttedato', null])
			},
			remove() {
				del(['tpsf.innvandretFraLand', 'tpsf.innvandretFraLandFlyttedato'])
			}
		},
		utvandretTilLand: {
			label: 'Utvandret til',
			checked: has('tpsf.utvandretTilLand'),
			add() {
				setMulti(['tpsf.utvandretTilLand', ''], ['tpsf.utvandretTilLandFlyttedato', null])
			},
			remove() {
				del(['tpsf.utvandretTilLand', 'tpsf.utvandretTilLandFlyttedato'])
			}
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
						regdato: null
					}
				]),
			remove: () => del('tpsf.identHistorikk')
		},
		kjonn: {
			label: 'Kjønn',
			checked: has('tpsf.kjonn'),
			add: () => set('tpsf.kjonn', ''),
			remove: () => del('tpsf.kjonn')
		},
		harMellomnavn: {
			label: 'Har mellomnavn',
			checked: has('tpsf.harMellomnavn'),
			add: () => set('tpsf.harMellomnavn', true),
			remove: () => del('tpsf.harMellomnavn')
		},
		sprakKode: {
			label: 'Språk',
			checked: has('tpsf.sprakKode'),
			add: () => set('tpsf.sprakKode', ''),
			remove: () => del('tpsf.sprakKode')
		},
		egenAnsattDatoFom: {
			label: 'Skjerming',
			checked: has('tpsf.egenAnsattDatoFom'),
			add() {
				setMulti(
					[
						'tpsf.egenAnsattDatoFom',
						_get(personFoerLeggTil, 'tpsf.egenAnsattDatoFom') || new Date()
					],
					['tpsf.egenAnsattDatoTom', undefined]
				)
			},
			remove() {
				del(['tpsf.egenAnsattDatoFom', 'tpsf.egenAnsattDatoTom'])
			}
		},
		erForsvunnet: {
			label: 'Forsvunnet',
			checked: has('tpsf.erForsvunnet'),
			add() {
				setMulti(['tpsf.erForsvunnet', true], ['tpsf.forsvunnetDato', null])
			},
			remove() {
				del(['tpsf.erForsvunnet', 'tpsf.forsvunnetDato'])
			}
		},
		harBankkontonr: {
			label: 'Bankkontonummer',
			checked: has('tpsf.harBankkontonr'),
			add() {
				setMulti(['tpsf.harBankkontonr', true], ['tpsf.bankkontonrRegdato', null])
			},
			remove() {
				del(['tpsf.harBankkontonr', 'tpsf.bankkontonrRegdato'])
			}
		},
		telefonnummer_1: {
			label: 'Telefonnummer',
			checked: has('tpsf.telefonnummer_1'),
			add() {
				setMulti(['tpsf.telefonLandskode_1', ''], ['tpsf.telefonnummer_1', ''])
			},
			remove() {
				del([
					'tpsf.telefonLandskode_1',
					'tpsf.telefonnummer_1',
					'tpsf.telefonLandskode_2',
					'tpsf.telefonnummer_2'
				])
			}
		},
		spesreg: {
			label: 'Diskresjonskode',
			checked: has('tpsf.spesreg') || has('tpsf.utenFastBopel'),
			add() {
				setMulti(['tpsf.spesreg', ''], ['tpsf.utenFastBopel', false])
			},
			remove() {
				del(['tpsf.spesreg', 'tpsf.utenFastBopel'])
			}
		},
		identtype: {
			label: 'Identtype',
			checked: has('tpsf.identtype'),
			add: () => setMulti(['tpsf.identtype', ''], ['tpsf.alder', personFoerLeggTil?.tpsf?.alder]),
			remove: () => del(['tpsf.identtype', 'tpsf.alder', 'tpsf.foedtEtter', 'tpsf.foedtFoer'])
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
					harMellomnavn: null
				}),
			remove: () => del('tpsf.vergemaal')
		},
		fullmakt: {
			label: 'Fullmakt',
			checked: has('tpsf.fullmakt'),
			add: () =>
				set('tpsf.fullmakt', {
					kilde: '',
					omraader: [],
					gyldigFom: null,
					gyldigTom: null,
					identType: null,
					harMellomnavn: null
				}),
			remove: () => del('tpsf.fullmakt')
		}
	}
}
