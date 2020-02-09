import React from 'react'
import { AttributtVelger } from './attributtVelger/AttributtVelger'

import { PersoninformasjonPanel } from './paneler/Personinformasjon'
import { AdressePanel } from './paneler/Adresse'
import { IdentifikasjonPanel } from './paneler/Identifikasjon'
import { FamilierelasjonPanel } from './paneler/Familierelasjoner'
import { ArbeidInntektPanel } from './paneler/ArbeidInntekt'
import { InstutisjonsoppholdPanel } from './paneler/Instutisjonsopphold'
import { KontaktReservasjonsPanel } from './paneler/KontaktReservasjon'
import { KontaktDoedsboPanel } from './paneler/KontaktDoedsbo'
import { ArenaPanel } from './paneler/Arena'
import { UdiPanel } from './paneler/Udi'

export const Steg1 = ({ stateModifier }) => {
	const checked = [
		PersoninformasjonPanel,
		AdressePanel,
		FamilierelasjonPanel,
		ArbeidInntektPanel,
		IdentifikasjonPanel,
		KontaktDoedsboPanel,
		InstutisjonsoppholdPanel,
		KontaktReservasjonsPanel,
		ArenaPanel,
		UdiPanel
	]
		.map(panel => ({
			label: panel.heading,
			values: stateModifier(panel.initialValues).checked
		}))
		.filter(v => v.values.length)

	return (
		<AttributtVelger checked={checked}>
			<PersoninformasjonPanel stateModifier={stateModifier} />
			<AdressePanel stateModifier={stateModifier} />
			<FamilierelasjonPanel stateModifier={stateModifier} />
			<ArbeidInntektPanel stateModifier={stateModifier} />
			<IdentifikasjonPanel stateModifier={stateModifier} />
			<KontaktDoedsboPanel stateModifier={stateModifier} />
			<InstutisjonsoppholdPanel stateModifier={stateModifier} />
			<KontaktReservasjonsPanel stateModifier={stateModifier} />
			<ArenaPanel stateModifier={stateModifier} />
			<UdiPanel stateModifier={stateModifier} />
		</AttributtVelger>
	)
}

Steg1.label = 'Velg egenskaper'
