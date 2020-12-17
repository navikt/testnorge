import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import organisasjon from '~/ducks/organisasjon'
import { Attributt, AttributtKategori } from '../Attributt'

export const OrganisasjonDetaljerPanel = ({ stateModifier }: any) => {
	const sm = stateModifier(OrganisasjonDetaljerPanel.initialValues)

	return (
		// @ts-ignore
		<Panel
			heading={OrganisasjonDetaljerPanel.heading}
			startOpen
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={() => sm.batchRemove('enhetstype')}
			// uncheckAttributeArray={sm.batchRemove}
			iconType="personinformasjon"
		>
			<AttributtKategori title="Enhetstype">
				<Attributt
					attr={sm.attrs.enhetstype}
					disabled={true}
					title="Det er obligatorisk å velge enhetstype for organisasjonen"
				/>
				<Attributt attr={sm.attrs.naeringskode} />
				<Attributt attr={sm.attrs.formaal} />
			</AttributtKategori>
			<AttributtKategori title="Kontaktdata">
				<Attributt attr={sm.attrs.telefon} />
				{/* <Attributt attr={sm.attrs.mobiltelefon} /> */}
				<Attributt attr={sm.attrs.epost} />
				<Attributt attr={sm.attrs.nettadresse} />
			</AttributtKategori>
			<AttributtKategori title="Adresser">
				{/* <Attributt attr={sm.attrs.adresser} /> */}
				<Attributt attr={sm.attrs.forretningsadresse} />
				<Attributt attr={sm.attrs.postadresse} />
			</AttributtKategori>
		</Panel>
	)
}

OrganisasjonDetaljerPanel.heading = 'Detaljer'

OrganisasjonDetaljerPanel.initialValues = ({ set, setMulti, del, has }: any) => {
	return {
		enhetstype: {
			label: 'Enhetstype',
			checked: has('organisasjon.enhetstype'),
			add: () => set('organisasjon.enhetstype', null),
			remove: () => del('organisasjon.enhetstype')
		},
		naeringskode: {
			label: 'Næringskode',
			checked: has('organisasjon.naeringskode'),
			add: () => set('organisasjon.naeringskode', null),
			remove: () => del('organisasjon.naeringskode')
		},
		formaal: {
			label: 'Formål',
			checked: has('organisasjon.formaal'),
			add: () => set('organisasjon.formaal', null),
			remove: () => del('organisasjon.formaal')
		},
		telefon: {
			label: 'Telefon',
			checked: has('organisasjon.telefon'),
			add: () => set('organisasjon.telefon', null),
			remove: () => del('organisasjon.telefon')
		},
		// mobiltelefon: {
		// 	label: 'Mobiltelefon',
		// 	checked: has('organisasjon.mobiltelefon'),
		// 	add: () => set('organisasjon.mobiltelefon', null),
		// 	remove: () => del('organisasjon.mobiltelefon')
		// },
		epost: {
			label: 'E-postadresse',
			checked: has('organisasjon.epost'),
			add: () => set('organisasjon.epost', null),
			remove: () => del('organisasjon.epost')
		},
		nettadresse: {
			label: 'Internettadresse',
			checked: has('organisasjon.nettadresse'),
			add: () => set('organisasjon.nettadresse', null),
			remove: () => del('organisasjon.nettadresse')
		},

		// adresser: {},
		// forretningsadresse: {
		// 	label: 'Forretningsadresse',
		// 	checked: has('organisasjon.forretningsadresse'),
		// 	add: () =>
		// 		set('organisasjon.forretningsadresse', {
		// 			landkode: 'NOR',
		// 			adresseLinje1: '',
		// 			adresseLinje2: '',
		// 			postnr: ''
		// 		}),
		// 	remove: () => del('organisasjon.forretningsadresse')
		// },

		forretningsadresse: {
			label: 'Forretningsadresse',
			checked: has('organisasjon.forretningsadresse'),
			add: () =>
				set('organisasjon.forretningsadresse', {
					// adressetype: 'FORRETNING_ADR',
					adresselinje: [null, null, null],
					postnr: '',
					kommunenr: '',
					landkode: 'NOR',
					poststed: null
				}),

			remove: () => del('organisasjon.forretningsadresse')
		},
		postadresse: {
			label: 'Postadresse',
			checked: has('organisasjon.postadresse'),
			add: () =>
				set('organisasjon.postadresse', {
					// adressetype: 'POST_ADR',
					adresselinje: [null, null, null],
					postnr: '',
					kommunenr: '',
					landkode: 'NOR',
					poststed: null
				}),

			remove: () => del('organisasjon.postadresse')
		}

		// ,
		// adresser: {
		// 	label: 'Postadresse',
		// 	checked: has('organisasjon.adresser'),
		// 	add: () =>
		// 		set('organisasjon.adresser', [
		// 			{
		// 				adressetype: 'POST_ADR',
		// 				adresselinje: [''],
		// 				postnr: '',
		// 				kommunenr: '',
		// 				landkode: 'NOR'
		// 			}
		// 		]),
		// 	remove: () => del('organisasjon.adresser')
		// }
		// postadresse: {
		// 	label: 'Postadresse',
		// 	checked: has('organisasjon.postadresse'),
		// 	add: () =>
		// 		set('organisasjon.postadresse', {
		// 			landkode: 'NOR',
		// 			adresseLinje1: '',
		// 			adresseLinje2: '',
		// 			postnr: ''
		// 		}),
		// 	remove: () => del('organisasjon.postadresse')
		// }
		// ,
		// adresser: {
		// 	label
		// }
	}
}
