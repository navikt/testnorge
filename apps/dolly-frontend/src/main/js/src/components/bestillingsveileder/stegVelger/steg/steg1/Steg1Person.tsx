// Steg1Person.tsx
import React, { useContext, useMemo } from 'react'
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
import { PensjonPanel } from './paneler/Pensjon'
import { ArbeidssoekerPanel } from './paneler/Arbeidssoeker'
import { MedlPanel } from './paneler/Medl'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useFormContext } from 'react-hook-form'
import { useStateModifierFns } from '@/components/bestillingsveileder/stateModifier'

type PanelWithMeta = React.FC<any> & { heading: string; initialValues: any }

const PANELS: PanelWithMeta[] = [
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
]

export const identFraTestnorge = (opts: BestillingsveilederContextType | undefined) =>
	!!(opts?.is?.importTestnorge || (opts?.is?.leggTil && opts?.identMaster === 'PDL'))

export interface Steg1PersonProps {
	stateModifier: ReturnType<typeof useStateModifierFns>
}

export const Steg1Person: React.FC<Steg1PersonProps> = ({ stateModifier }) => {
	const ctx = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const { watch } = useFormContext()
	const formValues = watch()
	const testnorgeIdent = identFraTestnorge(ctx)
	const personFoerLeggTil = ctx?.personFoerLeggTil
	const leggTil = ctx?.is?.leggTil || ctx?.is?.leggTilPaaGruppe
	const removeAlder = !!(personFoerLeggTil || leggTil)

	const checked = useMemo(
		() =>
			PANELS.map((panel) => {
				const res = stateModifier(panel.initialValues)
				return {
					label: panel.heading,
					values: res.checked.filter((v) => !(removeAlder && v === 'Alder')),
				}
			}).filter((g) => g.values.length),
		[stateModifier, removeAlder],
	)

	return (
		<AttributtVelger checked={checked}>
			<PersoninformasjonPanel stateModifier={stateModifier} testnorgeIdent={testnorgeIdent} />
			<AdressePanel stateModifier={stateModifier} formValues={formValues} />
			<FamilierelasjonPanel stateModifier={stateModifier} formValues={formValues} />
			<IdentifikasjonPanel stateModifier={stateModifier} formValues={formValues} />
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
