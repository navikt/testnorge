import Panel from '@/components/ui/panel/Panel'
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
			<AttributtKategori title="Organisasjon" attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.enhetstype}
					disabled={true}
					title="Det er obligatorisk å velge enhetstype for organisasjonen"
				/>
				<Attributt attr={sm.attrs.naeringskode} />
				<Attributt attr={sm.attrs.sektorkode} />
				<Attributt attr={sm.attrs.formaal} />
				<Attributt attr={sm.attrs.stiftelsesdato} />
				<Attributt attr={sm.attrs.maalform} />
			</AttributtKategori>
			<AttributtKategori title="Kontaktdata" attr={sm.attrs}>
				<Attributt attr={sm.attrs.telefon} />
				<Attributt attr={sm.attrs.epost} />
				<Attributt attr={sm.attrs.nettside} />
			</AttributtKategori>
			<AttributtKategori title="Adresser" attr={sm.attrs}>
				<Attributt attr={sm.attrs.forretningsadresse} />
				<Attributt attr={sm.attrs.postadresse} />
			</AttributtKategori>
		</Panel>
	)
}

OrganisasjonDetaljerPanel.heading = 'Detaljer'

OrganisasjonDetaljerPanel.initialValues = ({ set, del, has }: any) => {
	const paths = {
		enhetstype: 'organisasjon.enhetstype',
		naeringskode: 'organisasjon.naeringskode',
		sektorkode: 'organisasjon.sektorkode',
		formaal: 'organisasjon.formaal',
		stiftelsesdato: 'organisasjon.stiftelsesdato',
		maalform: 'organisasjon.maalform',
		telefon: 'organisasjon.telefon',
		epost: 'organisasjon.epost',
		nettside: 'organisasjon.nettside',
		forretningsadresse: 'organisasjon.forretningsadresse',
		postadresse: 'organisasjon.postadresse',
	}
	return {
		enhetstype: {
			label: 'Enhetstype',
			checked: has(paths.enhetstype),
			add: () => set(paths.enhetstype, ''),
			remove: () => del(paths.enhetstype),
		},
		naeringskode: {
			label: 'Næringskode',
			checked: has(paths.naeringskode),
			add: () => set(paths.naeringskode, ''),
			remove: () => del(paths.naeringskode),
		},
		sektorkode: {
			label: 'Sektorkode',
			checked: has(paths.sektorkode),
			add: () => set(paths.sektorkode, ''),
			remove: () => del(paths.sektorkode),
		},
		formaal: {
			label: 'Formål',
			checked: has(paths.formaal),
			add: () => set(paths.formaal, ''),
			remove: () => del(paths.formaal),
		},
		stiftelsesdato: {
			label: 'Stiftelsesdato',
			checked: has(paths.stiftelsesdato),
			add: () => set(paths.stiftelsesdato, ''),
			remove: () => del(paths.stiftelsesdato),
		},
		maalform: {
			label: 'Målform',
			checked: has(paths.maalform),
			add: () => set(paths.maalform, ''),
			remove: () => del(paths.maalform),
		},
		telefon: {
			label: 'Telefon',
			checked: has(paths.telefon),
			add: () => set(paths.telefon, ''),
			remove: () => del(paths.telefon),
		},
		epost: {
			label: 'E-postadresse',
			checked: has(paths.epost),
			add: () => set(paths.epost, ''),
			remove: () => del(paths.epost),
		},
		nettside: {
			label: 'Internettadresse',
			checked: has(paths.nettside),
			add: () => set(paths.nettside, ''),
			remove: () => del(paths.nettside),
		},
		forretningsadresse: {
			label: 'Forretningsadresse',
			checked: has(paths.forretningsadresse),
			add: () =>
				set(paths.forretningsadresse, {
					adresselinjer: ['', '', ''],
					postnr: '',
					kommunenr: '',
					landkode: 'NO',
					poststed: '',
				}),
			remove: () => del(paths.forretningsadresse),
		},
		postadresse: {
			label: 'Postadresse',
			checked: has(paths.postadresse),
			add: () =>
				set(paths.postadresse, {
					adresselinjer: ['', '', ''],
					postnr: '',
					kommunenr: '',
					landkode: 'NO',
					poststed: '',
				}),
			remove: () => del(paths.postadresse),
		},
	}
}
