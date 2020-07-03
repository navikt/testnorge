import React, { useContext } from 'react'
import { AttributtVelger } from './attributtVelger/AttributtVelger'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import { PersoninformasjonPanel } from './paneler/Personinformasjon'
import { AdressePanel } from './paneler/Adresse'
import { IdentifikasjonPanel } from './paneler/Identifikasjon'
import { FamilierelasjonPanel } from './paneler/Familierelasjoner'
import { ArbeidInntektPanel } from './paneler/ArbeidInntekt'
import { InstitusjonsoppholdPanel } from './paneler/Institusjonsopphold'
import { KontaktReservasjonsPanel } from './paneler/KontaktReservasjon'
import { KontaktDoedsboPanel } from './paneler/KontaktDoedsbo'
import { ArenaPanel } from './paneler/Arena'
import { UdiPanel } from './paneler/Udi'
import { BrregPanel } from './paneler/Brreg'
import { DokarkivPanel } from './paneler/Dokarkiv'

export const Steg1 = ({ stateModifier }) => {
	const opts = useContext(BestillingsveilederContext)
	const leggTil = opts.is.leggTil

	const checked = [
		PersoninformasjonPanel,
		AdressePanel,
		FamilierelasjonPanel,
		ArbeidInntektPanel,
		BrregPanel,
		IdentifikasjonPanel,
		KontaktDoedsboPanel,
		InstitusjonsoppholdPanel,
		KontaktReservasjonsPanel,
		ArenaPanel,
		UdiPanel,
		DokarkivPanel
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
			<BrregPanel stateModifier={stateModifier} />
			<IdentifikasjonPanel stateModifier={stateModifier} />
			<KontaktDoedsboPanel stateModifier={stateModifier} />
			<InstitusjonsoppholdPanel stateModifier={stateModifier} />
			<KontaktReservasjonsPanel stateModifier={stateModifier} />
			<ArenaPanel stateModifier={stateModifier} />
			<UdiPanel stateModifier={stateModifier} />
			<DokarkivPanel stateModifier={stateModifier} />
		</AttributtVelger>
	)
}

Steg1.label = 'Velg egenskaper'
