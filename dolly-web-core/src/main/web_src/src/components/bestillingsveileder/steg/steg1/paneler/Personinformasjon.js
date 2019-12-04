import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import {
	Attributt,
	AttributtKategori
} from '~/components/bestillingsveileder/AttributtVelger/Attributt'
import { subYears } from 'date-fns'

export const PersoninformasjonPanel = ({ stateModifier }) => {
	const sm = stateModifier(PersoninformasjonPanel.initialValues)

	return (
		<Panel
			heading={PersoninformasjonPanel.heading}
			startOpen
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
		>
			<AttributtKategori title="Alder">
				<Attributt attr={sm.attrs.foedtEtter} />
				<Attributt attr={sm.attrs.foedtFoer} />
				<Attributt attr={sm.attrs.doedsdato} />
			</AttributtKategori>

			<AttributtKategori title="Nasjonalitet">
				<Attributt attr={sm.attrs.statsborgerskap} />
				<Attributt attr={sm.attrs.innvandretFraLand} />
				<Attributt attr={sm.attrs.utvandretTilLand} />
			</AttributtKategori>

			<AttributtKategori title="Diverse">
				<Attributt attr={sm.attrs.identHistorikk} />
				<Attributt attr={sm.attrs.kjonn} />
				<Attributt attr={sm.attrs.harMellomnavn} />
				<Attributt attr={sm.attrs.sivilstand} />
				<Attributt attr={sm.attrs.sprakKode} />
				<Attributt attr={sm.attrs.egenAnsattDatoFom} />
				<Attributt attr={sm.attrs.spesreg} />
				<Attributt attr={sm.attrs.erForsvunnet} />
			</AttributtKategori>
		</Panel>
	)
}

PersoninformasjonPanel.heading = 'Personinformasjon'

PersoninformasjonPanel.initialValues = ({ set, setMulti, del, has }) => ({
	foedtEtter: {
		label: 'Født etter',
		checked: has('tpsf.foedtEtter'),
		add: () => set('tpsf.foedtEtter', subYears(new Date(), 80)),
		remove: () => del('tpsf.foedtEtter')
	},
	foedtFoer: {
		label: 'Født før',
		checked: has('tpsf.foedtFoer'),
		add: () => set('tpsf.foedtFoer', new Date()),
		remove: () => del('tpsf.foedtFoer')
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
			setMulti(['tpsf.statsborgerskap', ''], ['tpsf.statsborgerskapRegdato', null])
		},
		remove() {
			del(['tpsf.statsborgerskap', 'tpsf.statsborgerskapRegdato'])
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
		label: 'Utvandret fra',
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
	sivilstand: {
		label: 'Sivilstand',
		checked: has('tpsf.sivilstand'),
		add: () => set('tpsf.sivilstand', ''),
		remove: () => del('tpsf.sivilstand')
	},
	sprakKode: {
		label: 'Språk',
		checked: has('tpsf.sprakKode'),
		add: () => set('tpsf.sprakKode', ''),
		remove: () => del('tpsf.sprakKode')
	},
	egenAnsattDatoFom: {
		label: 'Egenansatt',
		checked: has('tpsf.egenAnsattDatoFom'),
		add: () => set('tpsf.egenAnsattDatoFom', new Date()),
		remove: () => del('tpsf.egenAnsattDatoFom')
	},
	spesreg: {
		label: 'Diskresjonskode',
		checked: has('tpsf.spesreg'),
		add() {
			setMulti(['tpsf.spesreg', ''], ['tpsf.utenFastBopel', false])
		},
		remove() {
			del(['tpsf.spesreg', 'tpsf.utenFastBopel'])
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
	}
})
