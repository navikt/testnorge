import React from 'react'
import Panel from '~/components/ui/panel/Panel'
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
				<Attributt attr={sm.attrs.epost} />
				<Attributt attr={sm.attrs.nettadresse} />
			</AttributtKategori>
			<AttributtKategori title="Adresser">
				<Attributt attr={sm.attrs.forretningsadresse} />
				<Attributt attr={sm.attrs.postadresse} />
			</AttributtKategori>
		</Panel>
	)
}

OrganisasjonDetaljerPanel.heading = 'Detaljer'

OrganisasjonDetaljerPanel.initialValues = ({ set, del, has }: any) => {
	return {
		enhetstype: {
			label: 'Enhetstype',
			checked: has('organisasjon.enhetstype'),
			add: () => set('organisasjon.enhetstype', ''),
			remove: () => del('organisasjon.enhetstype')
		},
		naeringskode: {
			label: 'Næringskode',
			checked: has('organisasjon.naeringskode'),
			add: () => set('organisasjon.naeringskode', ''),
			remove: () => del('organisasjon.naeringskode')
		},
		formaal: {
			label: 'Formål',
			checked: has('organisasjon.formaal'),
			add: () => set('organisasjon.formaal', ''),
			remove: () => del('organisasjon.formaal')
		},
		telefon: {
			label: 'Telefon',
			checked: has('organisasjon.telefon'),
			add: () => set('organisasjon.telefon', ''),
			remove: () => del('organisasjon.telefon')
		},
		epost: {
			label: 'E-postadresse',
			checked: has('organisasjon.epost'),
			add: () => set('organisasjon.epost', ''),
			remove: () => del('organisasjon.epost')
		},
		nettadresse: {
			label: 'Internettadresse',
			checked: has('organisasjon.nettadresse'),
			add: () => set('organisasjon.nettadresse', ''),
			remove: () => del('organisasjon.nettadresse')
		},
		forretningsadresse: {
			label: 'Forretningsadresse',
			checked: has('organisasjon.forretningsadresse'),
			add: () =>
				set('organisasjon.forretningsadresse', {
					adresselinjer: ['', '', ''],
					postnr: '',
					kommunenr: '',
					landkode: 'NOR',
					poststed: ''
				}),
			remove: () => del('organisasjon.forretningsadresse')
		},
		postadresse: {
			label: 'Postadresse',
			checked: has('organisasjon.postadresse'),
			add: () =>
				set('organisasjon.postadresse', {
					adresselinjer: ['', '', ''],
					postnr: '',
					kommunenr: '',
					landkode: 'NOR',
					poststed: ''
				}),
			remove: () => del('organisasjon.postadresse')
		}
	}
}
