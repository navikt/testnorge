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
			uncheckAttributeArray={sm.batchRemove}
			iconType="personinformasjon"
		>
			<AttributtKategori title="Organisasjon">
				<Attributt
					attr={sm.attrs.organisasjonsform}
					disabled={true}
					title="Det er obligatorisk å velge organisasjonsform for organisasjonen"
				/>
				<Attributt attr={sm.attrs.naeringskode} />
				<Attributt attr={sm.attrs.formaal} />
			</AttributtKategori>
			<AttributtKategori title="Adresser">
				<Attributt attr={sm.attrs.forretningsadresse} />
				<Attributt attr={sm.attrs.postadresse} />
			</AttributtKategori>
			<AttributtKategori title="Kontaktdata">
				<Attributt attr={sm.attrs.telefon} />
				<Attributt attr={sm.attrs.mobiltelefon} />
				<Attributt attr={sm.attrs.epost} />
				<Attributt attr={sm.attrs.nettadresse} />
			</AttributtKategori>
		</Panel>
	)
}

OrganisasjonDetaljerPanel.heading = 'Detaljer'

OrganisasjonDetaljerPanel.initialValues = ({ set, del, has }: any) => {
	return {
		organisasjonsform: {
			label: 'Organisasjonsform',
			checked: has('organisasjon.organisasjonsform'),
			add: () => set('organisasjon.organisasjonsform', null),
			remove: () => del('organisasjon.organisasjonsform')
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
		mobiltelefon: {
			label: 'Mobiltelefon',
			checked: has('organisasjon.mobiltelefon'),
			add: () => set('organisasjon.mobiltelefon', null),
			remove: () => del('organisasjon.mobiltelefon')
		},
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
		forretningsadresse: {
			label: 'Forretningsadresse',
			checked: has('organisasjon.forretningsadresse'),
			add: () =>
				set('organisasjon.forretningsadresse', {
					landkode: 'NOR',
					adresseLinje1: '',
					adresseLinje2: '',
					postnr: ''
				}),
			remove: () => del('organisasjon.forretningsadresse')
		},
		postadresse: {
			label: 'Postadresse',
			checked: has('organisasjon.postadresse'),
			add: () =>
				set('organisasjon.postadresse', {
					landkode: 'NOR',
					adresseLinje1: '',
					adresseLinje2: '',
					postnr: ''
				}),
			remove: () => del('organisasjon.postadresse')
		}
	}
}
