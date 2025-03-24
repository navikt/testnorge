import Panel from '@/components/ui/panel/Panel'
import {
	Attributt,
	AttributtKategori,
} from '@/components/bestillingsveileder/stegVelger/steg/steg1/Attributt'
import {
	getInitialAdressebeskyttelse,
	getInitialBostedsadresse,
	getInitialKontaktadresse,
	getInitialOppholdsadresse,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import { adresseAttributter } from '@/components/fagsystem/pdlf/form/partials/adresser/Adresser'

export const AdressePanel = ({ stateModifier, formValues }: any) => {
	const sm = stateModifier(AdressePanel.initialValues)
	return (
		// @ts-ignore
		<Panel
			heading={AdressePanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="adresse"
			startOpen={harValgtAttributt(formValues, adresseAttributter)}
		>
			<AttributtKategori title="Adresser" attr={sm.attrs}>
				<Attributt attr={sm.attrs.bostedsadresse} />
				<Attributt attr={sm.attrs.oppholdsadresse} />
				<Attributt attr={sm.attrs.kontaktadresse} />
			</AttributtKategori>

			<AttributtKategori title="Adressebeskyttelse" attr={sm.attrs}>
				<Attributt attr={sm.attrs.adressebeskyttelse} />
			</AttributtKategori>
		</Panel>
	)
}

AdressePanel.heading = 'Adresser'

AdressePanel.initialValues = ({ set, opts, del, has }: any) => {
	const { identtype, identMaster } = opts

	const initialMaster = identMaster === 'PDL' || identtype === 'NPID' ? 'PDL' : 'FREG'

	const paths = {
		bostedadresse: 'pdldata.person.bostedsadresse',
		oppholdsadresse: 'pdldata.person.oppholdsadresse',
		kontaktadresse: 'pdldata.person.kontaktadresse',
		adressebeskyttelse: 'pdldata.person.adressebeskyttelse',
	}

	return {
		bostedsadresse: {
			label: 'Bostedsadresse',
			checked: has(paths.bostedadresse),
			add: () => {
				set(paths.bostedadresse, [getInitialBostedsadresse(initialMaster)])
			},
			remove: () => {
				del(paths.bostedadresse)
			},
		},
		oppholdsadresse: {
			label: 'Oppholdsadresse',
			checked: has(paths.oppholdsadresse),
			add() {
				set(paths.oppholdsadresse, [getInitialOppholdsadresse(initialMaster)])
			},
			remove() {
				del(paths.oppholdsadresse)
			},
		},
		kontaktadresse: {
			label: 'Kontaktadresse',
			checked: has(paths.kontaktadresse),
			add() {
				set(paths.kontaktadresse, [getInitialKontaktadresse(initialMaster)])
			},
			remove() {
				del(paths.kontaktadresse)
			},
		},
		adressebeskyttelse: {
			label: 'Adressebeskyttelse (kode 6/7)',
			checked: has(paths.adressebeskyttelse),
			add() {
				set(paths.adressebeskyttelse, [getInitialAdressebeskyttelse(initialMaster)])
			},
			remove() {
				del(paths.adressebeskyttelse)
			},
		},
	}
}
