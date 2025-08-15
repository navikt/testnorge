import React, { useContext } from 'react'
import Panel from '@/components/ui/panel/Panel'
import { AttributtKategori } from '@/components/bestillingsveileder/stegVelger/steg/steg1/AttributtKategori'
import { Attributt } from '@/components/bestillingsveileder/stegVelger/steg/steg1/Attributt'
import {
	getInitialAdressebeskyttelse,
	getInitialBostedsadresse,
	getInitialKontaktadresse,
	getInitialOppholdsadresse,
	initialDeltBosted,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import { adresseAttributter } from '@/components/fagsystem/pdlf/form/partials/adresser/Adresser'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useStateModifierFns } from '@/components/bestillingsveileder/stateModifier'

export const ADRESSE_PANEL_HEADING = 'Adresser'

interface AdressePanelProps {
	stateModifier: ReturnType<typeof useStateModifierFns>
	formValues: any
}

export const AdressePanel: React.FC<AdressePanelProps> & {
	initialValues: any
} = ({ stateModifier, formValues }) => {
	const ctx = useContext(BestillingsveilederContext)
	const { currentBruker } = useCurrentBruker()
	const sm = stateModifier(adresseInitialValues)
	const testNorgePerson = ctx?.identMaster === 'PDL'
	const bankId = currentBruker?.brukertype === 'BANKID'

	return (
		<Panel
			heading={ADRESSE_PANEL_HEADING}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="adresse"
			startOpen={harValgtAttributt(formValues, adresseAttributter)}
		>
			<AttributtKategori title="Adresser" attr={sm.attrs}>
				<Attributt item={sm.attrs.bostedsadresse} />
				<Attributt item={sm.attrs.oppholdsadresse} />
				<Attributt item={sm.attrs.kontaktadresse} />
			</AttributtKategori>

			<AttributtKategori title="Delt bosted" attr={sm.attrs}>
				<Attributt
					item={sm.attrs.deltBosted}
					infoTekst={
						'Opplysningstypen “Delt bosted” legges til på barnet(barna), men kan bestilles på hovedperson/partner/barn. Vilkåret er at barn(a) har to foreldre med forskjellig norsk bostedsadresse. Gjelder kun for master FREG.'
					}
					vis={!testNorgePerson}
				/>
			</AttributtKategori>

			<AttributtKategori title="Adressebeskyttelse" attr={sm.attrs}>
				<Attributt
					item={sm.attrs.adressebeskyttelse}
					infoTekst={bankId ? 'Kun kode 6 tilgjengelig for BankID-bruker.' : undefined}
				/>
			</AttributtKategori>
		</Panel>
	)
}

const adresseInitialValues = ({ set, del, has, opts }: any) => {
	const { identtype, identMaster } = opts || {}
	const initialMaster = identMaster === 'PDL' || identtype === 'NPID' ? 'PDL' : 'FREG'

	const paths = {
		bostedsadresse: 'pdldata.person.bostedsadresse',
		oppholdsadresse: 'pdldata.person.oppholdsadresse',
		kontaktadresse: 'pdldata.person.kontaktadresse',
		adressebeskyttelse: 'pdldata.person.adressebeskyttelse',
		deltBosted: 'pdldata.person.deltBosted',
	}

	return {
		bostedsadresse: {
			label: 'Bostedsadresse',
			checked: has(paths.bostedsadresse),
			add: () => set(paths.bostedsadresse, [getInitialBostedsadresse(initialMaster)]),
			remove: () => del(paths.bostedsadresse),
		},
		oppholdsadresse: {
			label: 'Oppholdsadresse',
			checked: has(paths.oppholdsadresse),
			add: () => set(paths.oppholdsadresse, [getInitialOppholdsadresse(initialMaster)]),
			remove: () => del(paths.oppholdsadresse),
		},
		kontaktadresse: {
			label: 'Kontaktadresse',
			checked: has(paths.kontaktadresse),
			add: () => set(paths.kontaktadresse, [getInitialKontaktadresse(initialMaster)]),
			remove: () => del(paths.kontaktadresse),
		},
		adressebeskyttelse: {
			label: 'Adressebeskyttelse (kode 6/7)',
			checked: has(paths.adressebeskyttelse),
			add: () => set(paths.adressebeskyttelse, [getInitialAdressebeskyttelse(initialMaster)]),
			remove: () => del(paths.adressebeskyttelse),
		},
		deltBosted: {
			label: 'Delt bosted (for barn)',
			checked: has(paths.deltBosted),
			add: () => set(paths.deltBosted, [initialDeltBosted]),
			remove: () => del(paths.deltBosted),
		},
	}
}

AdressePanel.initialValues = adresseInitialValues
