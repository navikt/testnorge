import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import {
	Attributt,
	AttributtKategori,
} from '~/components/bestillingsveileder/stegVelger/steg/steg1/Attributt'
import {
	initialAdressebeskyttelse,
	initialBostedsadresse,
	initialKontaktadresse,
	initialOppholdsadresse,
} from '~/components/fagsystem/pdlf/form/initialValues'
import { harValgtAttributt } from '~/components/ui/form/formUtils'
import { adresseAttributter } from '~/components/fagsystem/pdlf/form/partials/adresser/Adresser'

export const AdressePanel = ({ stateModifier, formikBag }: any) => {
	const sm = stateModifier(AdressePanel.initialValues)
	return (
		// @ts-ignore
		<Panel
			heading={AdressePanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="adresse"
			startOpen={harValgtAttributt(formikBag.values, adresseAttributter)}
		>
			<AttributtKategori title="Adresser">
				<Attributt attr={sm.attrs.bostedsadresse} />
				<Attributt attr={sm.attrs.oppholdsadresse} />
				<Attributt attr={sm.attrs.kontaktadresse} />
			</AttributtKategori>

			<AttributtKategori title="Adressebeskyttelse">
				<Attributt attr={sm.attrs.adressebeskyttelse} />
			</AttributtKategori>
		</Panel>
	)
}

AdressePanel.heading = 'Adresser'

AdressePanel.initialValues = ({ set, del, has }: any) => {
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
			add() {
				set(paths.bostedadresse, [initialBostedsadresse])
			},
			remove() {
				del(paths.bostedadresse)
			},
		},
		oppholdsadresse: {
			label: 'Oppholdsadresse',
			checked: has(paths.oppholdsadresse),
			add() {
				set(paths.oppholdsadresse, [initialOppholdsadresse])
			},
			remove() {
				del(paths.oppholdsadresse)
			},
		},
		kontaktadresse: {
			label: 'Kontaktadresse',
			checked: has(paths.kontaktadresse),
			add() {
				set(paths.kontaktadresse, [initialKontaktadresse])
			},
			remove() {
				del(paths.kontaktadresse)
			},
		},
		adressebeskyttelse: {
			label: 'Adressebeskyttelse',
			checked: has(paths.adressebeskyttelse),
			add() {
				set(paths.adressebeskyttelse, [initialAdressebeskyttelse])
			},
			remove() {
				del(paths.adressebeskyttelse)
			},
		},
	}
}
