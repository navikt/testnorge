import React, { Context } from 'react'

export interface BestillingsveilederContextType {
	gruppeId?: string
	initialValues: any
	gruppe?: { id?: string; navn?: string }
	personFoerLeggTil?: string
	identMaster?: string
	antall?: number
	identtype?: string
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
