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

export const identFraTestnorge = (opts: any) => {
	if (opts?.is?.importTestnorge) return true
	return opts?.is?.leggTil && opts?.identMaster === 'PDL'
}

const leggTilPaaTpsfIdent = (opts: any) => {
	return opts?.is?.leggTil && opts?.identMaster === 'TPSF'
}

export const Steg1Person = ({ stateModifier }: any) => {
	const opts = useContext(BestillingsveilederContext)
	const testnorgeIdent = identFraTestnorge(opts)
	const tpsfIdent = leggTilPaaTpsfIdent(opts)
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
			{!testnorgeIdent && !tpsfIdent && <PersoninformasjonPanel stateModifier={stateModifier} />}
			{!testnorgeIdent && !tpsfIdent && <AdressePanel stateModifier={stateModifier} />}
			{!testnorgeIdent && !tpsfIdent && <FamilierelasjonPanel stateModifier={stateModifier} />}
			{!testnorgeIdent && !tpsfIdent && <IdentifikasjonPanel stateModifier={stateModifier} />}
			{!testnorgeIdent && !tpsfIdent && <KontaktDoedsboPanel stateModifier={stateModifier} />}
			<ArbeidInntektPanel
				stateModifier={stateModifier}
				testnorgeIdent={testnorgeIdent}
				tpsfIdent={tpsfIdent}
			/>
			<ArenaPanel stateModifier={stateModifier} />
			<SykdomPanel stateModifier={stateModifier} />
			<BrregPanel stateModifier={stateModifier} />
			<InstitusjonsoppholdPanel stateModifier={stateModifier} />
			<KontaktReservasjonsPanel stateModifier={stateModifier} />
			<UdiPanel stateModifier={stateModifier} testnorgeIdent={testnorgeIdent} />
			<DokarkivPanel stateModifier={stateModifier} />
		</AttributtVelger>
	)
}
