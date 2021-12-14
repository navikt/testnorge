import React from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { validation } from './validation'
import Panel from '~/components/ui/panel/Panel'
import { erForste, panelError } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FalskIdentitet } from './partials/falskIdentitet/FalskIdentitet'
import { UtenlandsId } from './partials/utenlandsId/UtenlandsId'
import { KontaktinformasjonForDoedsbo } from './partials/kontaktinformasjonForDoedsbo/KontaktinformasjonForDoedsbo'
import { UtenlandskAdresse } from './partials/adresser/adressetyper/UtenlandskAdresse'
import { Adresser } from '~/components/fagsystem/pdlf/form/partials/adresser/Adresser'

const adresseAttributter = [
	'pdldata.person.bostedsadresse',
	'pdldata.person.oppholdsadresse',
	'pdldata.person.kontaktadresse',
	'pdldata.person.adressebeskyttelse',
]
const identifikasjonAttributt = [
	'pdldata.person.falskIdentitet',
	'pdldata.person.utenlandskIdentifikasjonsnummer',
]
const doedsboAttributt = 'pdldata.person.kontaktinformasjonForDoedsbo'

export const PdlfForm = ({ formikBag }) => (
	<React.Fragment>
		<Vis attributt={adresseAttributter}>
			<Panel
				heading="Adresser"
				hasErrors={panelError(formikBag, adresseAttributter)}
				iconType="adresse"
				startOpen={() => erForste(formikBag.values, adresseAttributter)}
			>
				<Adresser formikBag={formikBag} />
			</Panel>
		</Vis>

		{/*<Vis attributt={bostedsadresseAttributt}>*/}
		{/*	<Panel*/}
		{/*		heading="Bostedsadresse"*/}
		{/*		hasErrors={panelError(formikBag, bostedsadresseAttributt)}*/}
		{/*		iconType="adresse"*/}
		{/*		startOpen={() => erForste(formikBag.values, bostedsadresseAttributt)}*/}
		{/*	>*/}
		{/*		<Kategori title="Utenlandsk boadresse" vis="pdldata.person.bostedsadresse">*/}
		{/*			<UtenlandskAdresse formikBag={formikBag} />*/}
		{/*		</Kategori>*/}
		{/*	</Panel>*/}
		{/*</Vis>*/}

		<Vis attributt={identifikasjonAttributt}>
			<Panel
				heading="Identifikasjon"
				hasErrors={panelError(formikBag, identifikasjonAttributt)}
				iconType="identifikasjon"
				startOpen={() => erForste(formikBag.values, identifikasjonAttributt)}
			>
				<Kategori title="Falsk identitet" vis="pdldata.person.falskIdentitet">
					<FalskIdentitet formikBag={formikBag} />
				</Kategori>
				<Kategori
					title="Utenlandsk identifikasjonsnummer"
					vis="pdldata.person.utenlandskIdentifikasjonsnummer"
					flex={false}
				>
					<UtenlandsId />
				</Kategori>
			</Panel>
		</Vis>

		<Vis attributt={doedsboAttributt}>
			<Panel
				heading="Kontaktinformasjon for dødsbo"
				hasErrors={panelError(formikBag, doedsboAttributt)}
				iconType="doedsbo"
				startOpen={() => erForste(formikBag.values, [doedsboAttributt])}
			>
				<KontaktinformasjonForDoedsbo formikBag={formikBag} />
			</Panel>
		</Vis>
	</React.Fragment>
)

PdlfForm.validation = validation
