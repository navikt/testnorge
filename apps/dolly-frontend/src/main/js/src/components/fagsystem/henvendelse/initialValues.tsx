export const henvendelseAttributt = 'henvendelse'
export const henvendelseMeldingerPath = `${henvendelseAttributt}.meldinger`
export const henvendelseSamtalereferaterPath = `${henvendelseAttributt}.samtalereferater`

export const initialSamtalereferat = {
	temagruppe: null,
	tema: null,
	enhet: null,
	fritekst: '',
	kjedeId: null,
	meldingsId: null,
}

export const initialMelding = {
	...initialSamtalereferat,
	tildelMeg: false,
}
