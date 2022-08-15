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
import { PensjonPanel } from '~/components/bestillingsveileder/stegVelger/steg/steg1/paneler/Pensjon'

export const identFraTestnorge = (opts: any) => {
	if (opts?.is?.importTestnorge) {
		return true
	}
	return opts?.is?.leggTil && opts?.identMaster === 'PDL'
}

const leggTilPaaTpsfIdent = (opts: any) => {
	return opts?.is?.leggTil && opts?.identMaster === 'TPSF'
}

export const Steg1Person = ({ formikBag, stateModifier }: any) => {
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
		PensjonPanel,
	]
		.map((panel) => ({
			label: panel.heading,
			values: stateModifier(panel.initialValues).checked,
		}))
		.filter((v) => v.values.length)

	return (
		<AttributtVelger checked={checked}>
			<PersoninformasjonPanel stateModifier={stateModifier} testnorgeIdent={testnorgeIdent} />
			{!testnorgeIdent && <AdressePanel stateModifier={stateModifier} formikBag={formikBag} />}
			{!testnorgeIdent && !tpsfIdent && (
				<FamilierelasjonPanel stateModifier={stateModifier} formikBag={formikBag} />
			)}
			{!testnorgeIdent && (
				<IdentifikasjonPanel stateModifier={stateModifier} formikBag={formikBag} />
			)}
			{!testnorgeIdent && (
				<KontaktDoedsboPanel stateModifier={stateModifier} formikBag={formikBag} />
			)}
			<ArbeidInntektPanel stateModifier={stateModifier} formikBag={formikBag} />
			<PensjonPanel stateModifier={stateModifier} formikBag={formikBag} />
			<ArenaPanel stateModifier={stateModifier} formikBag={formikBag} />
			<SykdomPanel stateModifier={stateModifier} formikBag={formikBag} />
			<BrregPanel stateModifier={stateModifier} formikBag={formikBag} />
			<InstitusjonsoppholdPanel stateModifier={stateModifier} formikBag={formikBag} />
			<KontaktReservasjonsPanel stateModifier={stateModifier} formikBag={formikBag} />
			<UdiPanel
				stateModifier={stateModifier}
				testnorgeIdent={testnorgeIdent}
				formikBag={formikBag}
			/>
			<DokarkivPanel stateModifier={stateModifier} formikBag={formikBag} />
		</AttributtVelger>
	)
}
