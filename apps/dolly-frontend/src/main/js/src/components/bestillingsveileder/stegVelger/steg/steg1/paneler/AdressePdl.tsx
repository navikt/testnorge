import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import {
	Attributt,
	AttributtKategori,
} from '~/components/bestillingsveileder/stegVelger/steg/steg1/Attributt'
import { initialBostedsadresse } from '~/components/fagsystem/pdlf/form/initialValues'

export const AdressePdlPanel = ({ stateModifier }) => {
	const sm = stateModifier(AdressePdlPanel.initialValues)
	return (
		<Panel
			heading={AdressePdlPanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="adresse"
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

AdressePdlPanel.heading = 'Adresser'

AdressePdlPanel.initialValues = ({ set, setMulti, del, has }) => ({
	bostedsadresse: {
		label: 'Bostedsadresse',
		checked: has('pdldata.person.bostedsadresse'),
		add() {
			set('pdldata.person.bostedsadresse', [initialBostedsadresse])
		},
		remove() {
			del('pdldata.person.bostedsadresse')
		},
	},
	oppholdsadresse: {
		label: 'Oppholdsadresse',
		checked: has('pdldata.person.oppholdsadresse'),
		add() {
			set('pdldata.person.oppholdsadresse', [
				{
					adressetype: null,
					kilde: 'Dolly',
					master: 'PDL',
					gjeldende: true,
				},
			])
		},
		remove() {
			del('pdldata.person.oppholdsadresse')
		},
	},
	kontaktadresse: {
		label: 'Kontaktadresse',
		checked: has('pdldata.person.kontaktadresse'),
		add() {
			set('pdldata.person.kontaktadresse', [
				{
					adressetype: null,
					kilde: 'Dolly',
					master: 'PDL',
					gjeldende: true,
				},
			])
		},
		remove() {
			del('pdldata.person.kontaktadresse')
		},
	},
	adressebeskyttelse: {
		label: 'Adressebeskyttelse',
		checked: has('pdldata.person.adressebeskyttelse'),
		add() {
			set('pdldata.person.adressebeskyttelse', [
				{
					gradering: null,
					kilde: 'Dolly',
					master: 'PDL',
					gjeldende: true,
				},
			])
		},
		remove() {
			del('pdldata.person.adressebeskyttelse')
		},
	},
})
