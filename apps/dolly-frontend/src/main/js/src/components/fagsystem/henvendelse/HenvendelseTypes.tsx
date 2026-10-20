export type SamtalereferatTypes = {
	temagruppe?: string
	tema?: string
	enhet?: string
	fritekst?: string
	kjedeId?: string
	meldingsId?: string
}

export type MeldingTypes = SamtalereferatTypes & {
	tildelMeg?: boolean
}

export type HenvendelseTypes = {
	meldinger?: MeldingTypes[]
	samtalereferater?: SamtalereferatTypes[]
}
