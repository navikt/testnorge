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
	const importTestnorge = opts.is.importTestnorge
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
			{!importTestnorge && <PersoninformasjonPanel stateModifier={stateModifier} />}
			{!importTestnorge && <AdressePanel stateModifier={stateModifier} />}
			{!importTestnorge && <FamilierelasjonPanel stateModifier={stateModifier} />}
			<ArbeidInntektPanel stateModifier={stateModifier} startOpen={importTestnorge} />
			{!importTestnorge && <ArenaPanel stateModifier={stateModifier} />}
			<SykdomPanel stateModifier={stateModifier} />
			<BrregPanel stateModifier={stateModifier} />
			{!importTestnorge && <IdentifikasjonPanel stateModifier={stateModifier} />}
			{!importTestnorge && <KontaktDoedsboPanel stateModifier={stateModifier} />}
			<InstitusjonsoppholdPanel stateModifier={stateModifier} />
			<KontaktReservasjonsPanel stateModifier={stateModifier} />
			<UdiPanel stateModifier={stateModifier} importTestnorge={importTestnorge} />
			<DokarkivPanel stateModifier={stateModifier} />
		</AttributtVelger>
	)
}
