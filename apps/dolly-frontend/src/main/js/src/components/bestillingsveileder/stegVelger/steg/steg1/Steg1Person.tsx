import React, { useContext } from 'react'
import { AttributtVelger } from './attributtVelger/AttributtVelger'
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
import { SykdomPanel } from './paneler/Sykdom'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'

export const Steg1Person = ({ stateModifier }: any) => {
	const opts = useContext(BestillingsveilederContext)
	const checked = [
		PersoninformasjonPanel,
		AdressePanel,
		FamilierelasjonPanel,
		ArbeidInntektPanel,
		SykdomPanel,
		BrregPanel,
		IdentifikasjonPanel,
		KontaktDoedsboPanel,
		InstitusjonsoppholdPanel,
		KontaktReservasjonsPanel,
		ArenaPanel,
		UdiPanel,
		DokarkivPanel,
	]
		.map((panel) => ({
			label: panel.heading,
			values: stateModifier(panel.initialValues).checked,
		}))
		.filter((v) => v.values.length)

	return (
		<AttributtVelger checked={checked}>
			{!opts.is.importTestnorge && <PersoninformasjonPanel stateModifier={stateModifier} />}
			{!opts.is.importTestnorge && <AdressePanel stateModifier={stateModifier} />}
			{!opts.is.importTestnorge && <FamilierelasjonPanel stateModifier={stateModifier} />}
			<ArbeidInntektPanel stateModifier={stateModifier} startOpen={opts.is.importTestnorge} />
			{!opts.is.importTestnorge && <ArenaPanel stateModifier={stateModifier} />}
			<SykdomPanel stateModifier={stateModifier} />
			<BrregPanel stateModifier={stateModifier} />
			<IdentifikasjonPanel stateModifier={stateModifier} />
			<KontaktDoedsboPanel stateModifier={stateModifier} />
			<InstitusjonsoppholdPanel stateModifier={stateModifier} />
			<KontaktReservasjonsPanel stateModifier={stateModifier} />
			<UdiPanel stateModifier={stateModifier} />
			<DokarkivPanel stateModifier={stateModifier} />
		</AttributtVelger>
	)
}
