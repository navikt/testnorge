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
	id2032?: boolean
	importPersoner?: Person[]
	tidligereBestillinger?: BestillingData[]
	opprettFraIdenter?: string[]
	mal?: { malNavn?: string }
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
}

const defaultContextValue: BestillingsveilederContextType = {
	initialValues: {},
}

export const BestillingsveilederContext: Context<BestillingsveilederContextType> =
	React.createContext(defaultContextValue)
