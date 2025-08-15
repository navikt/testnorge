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
	initialDeltBosted,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import { adresseAttributter } from '@/components/fagsystem/pdlf/form/partials/adresser/Adresser'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { useContext } from 'react'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'

export const AdressePanel = ({ stateModifier, formValues }: any) => {
	const sm = stateModifier(AdressePanel.initialValues)
	const opts: any = useContext(BestillingsveilederContext)
	const testNorgePerson = opts?.identMaster === 'PDL'

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

			<AttributtKategori title="Delt bosted" attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.deltBosted}
					infoTekst={
						'Opplysningstypen “Delt bosted” legges til på barnet(barna), men kan bestilles på hovedperson/partner/barn. ' +
						'Vilkåret er at barn(a) har to foreldre med forskjellig norsk bostedsadresse.  Gjelder kun for master FREG.'
					}
					vis={!testNorgePerson}
				/>
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
	const { currentBruker } = useCurrentBruker()
	const bankIdBruker = currentBruker?.brukertype === 'BANKID'

	const initialMaster = identMaster === 'PDL' || identtype === 'NPID' ? 'PDL' : 'FREG'

	const paths = {
		bostedadresse: 'pdldata.person.bostedsadresse',
		oppholdsadresse: 'pdldata.person.oppholdsadresse',
		kontaktadresse: 'pdldata.person.kontaktadresse',
		adressebeskyttelse: 'pdldata.person.adressebeskyttelse',
		deltBosted: 'pdldata.person.deltBosted',
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
			label: bankIdBruker ? 'Adressebeskyttelse (kode 6)' : 'Adressebeskyttelse (kode 6/7)',
			checked: has(paths.adressebeskyttelse),
			add() {
				set(paths.adressebeskyttelse, [getInitialAdressebeskyttelse(initialMaster)])
			},
			remove() {
				del(paths.adressebeskyttelse)
			},
		},
		deltBosted: {
			label: 'Delt bosted (for barn)',
			checked: has(paths.deltBosted),
			add() {
				set(paths.deltBosted, [initialDeltBosted])
			},
			remove() {
				del(paths.deltBosted)
			},
		},
	}
}
