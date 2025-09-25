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
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { PensjonPanel } from '@/components/bestillingsveileder/stegVelger/steg/steg1/paneler/Pensjon'
import { ArbeidssoekerPanel } from '@/components/bestillingsveileder/stegVelger/steg/steg1/paneler/Arbeidssoeker'
import { MedlPanel } from '@/components/bestillingsveileder/stegVelger/steg/steg1/paneler/Medl'
import { useFormContext } from 'react-hook-form'
import { NavAnsattPanel } from '@/components/bestillingsveileder/stegVelger/steg/steg1/paneler/NavAnsatt'

export const identFraTestnorge = (opts: any) => {
	if (opts?.is?.importTestnorge) {
		return true
	}
	return opts?.is?.leggTil && opts?.identMaster === 'PDL'
}

export const Steg1Person = ({ stateModifier }: any) => {
	const opts: any = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const { watch } = useFormContext()
	const testnorgeIdent = identFraTestnorge(opts)
	const personFoerLeggTil = opts?.personFoerLeggTil
	const leggTil = opts?.is?.leggTil || opts?.is?.leggTilPaaGruppe

	const checked = [
		PersoninformasjonPanel,
		AdressePanel,
		FamilierelasjonPanel,
		ArbeidInntektPanel,
		ArbeidssoekerPanel,
		SykdomPanel,
		MedlPanel,
		BrregPanel,
		IdentifikasjonPanel,
		KontaktDoedsboPanel,
		InstitusjonsoppholdPanel,
		KontaktReservasjonsPanel,
		ArenaPanel,
		UdiPanel,
		DokarkivPanel,
		PensjonPanel,
		NavAnsattPanel,
	]
		.map((panel) => ({
			label: panel.heading,
			values: stateModifier(panel.initialValues).checked?.filter(
				(val: string) =>
					(!personFoerLeggTil && !leggTil) || ((personFoerLeggTil || leggTil) && val !== 'Alder'),
			),
		}))
		.filter((v) => v.values.length)

	const formValues = watch()

	return (
		<AttributtVelger checked={checked}>
			<PersoninformasjonPanel stateModifier={stateModifier} testnorgeIdent={testnorgeIdent} />
			<AdressePanel stateModifier={stateModifier} formValues={formValues} />
			<FamilierelasjonPanel stateModifier={stateModifier} formValues={formValues} />
			<IdentifikasjonPanel stateModifier={stateModifier} formValues={formValues} />
			<NavAnsattPanel stateModifier={stateModifier} formValues={formValues} />
			{!testnorgeIdent && (
				<KontaktDoedsboPanel stateModifier={stateModifier} formValues={formValues} />
			)}
			<ArbeidInntektPanel stateModifier={stateModifier} formValues={formValues} />
			<ArbeidssoekerPanel stateModifier={stateModifier} formValues={formValues} />
			<PensjonPanel stateModifier={stateModifier} formValues={formValues} />
			<ArenaPanel stateModifier={stateModifier} formValues={formValues} />
			<SykdomPanel stateModifier={stateModifier} formValues={formValues} />
			<BrregPanel stateModifier={stateModifier} formValues={formValues} />
			<InstitusjonsoppholdPanel stateModifier={stateModifier} formValues={formValues} />
			<KontaktReservasjonsPanel stateModifier={stateModifier} formValues={formValues} />
			<MedlPanel stateModifier={stateModifier} formValues={formValues} />
			<UdiPanel
				stateModifier={stateModifier}
				testnorgeIdent={testnorgeIdent}
				formValues={formValues}
			/>
			<DokarkivPanel stateModifier={stateModifier} formValues={formValues} />
		</AttributtVelger>
	)
}
