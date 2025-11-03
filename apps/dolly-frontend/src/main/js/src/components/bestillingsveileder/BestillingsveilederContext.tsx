import React, { Context } from 'react'
import { Person } from '@/components/fagsystem/pdlf/PdlTypes'
import { BestillingData } from '@/components/fagsystem/inntektsmelding/InntektsmeldingTypes'

export interface BestillingsveilederContextType {
	gruppeId?: string
	initialValues: any
	gruppe?: { id?: string; navn?: string }
	personFoerLeggTil?: string
	identMaster?: string
	antall?: number
	identtype?: string
	setIdenttype?: (identtype: string) => void
	setGruppeId?: (gruppeId: number) => void
	setMal?: (mal: any | undefined) => void
	updateContext?: (patch: Partial<BestillingsveilederContextType>) => void
	id2032?: boolean
	importPersoner?: Person[]
	tidligereBestillinger?: BestillingData[]
	opprettFraIdenter?: string[]
	mal?: { malNavn?: string; [key: string]: any }
	is?: {
		nyBestilling?: boolean
		nyBestillingFraMal?: boolean
		opprettFraIdenter?: boolean
		leggTil?: boolean
		leggTilPaaGruppe?: boolean
		nyOrganisasjon?: boolean
		nyOrganisasjonFraMal?: boolean
		nyStandardOrganisasjon?: boolean
		importTestnorge?: boolean
	}
	timedOutFagsystemer?: string[]
}

const defaultContextValue: BestillingsveilederContextType = {
	initialValues: {},
	timedOutFagsystemer: [],
	setIdenttype: () => {},
	setGruppeId: () => {},
	setMal: () => {},
	updateContext: () => {},
}

export const BestillingsveilederContext: Context<BestillingsveilederContextType> =
	React.createContext(defaultContextValue)
