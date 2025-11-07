export type FullmaktType = {
	gyldigFraOgMed: string
	gyldigTilOgMed?: string
	fullmaktsgiver: string
	fullmaktsgiverNavn: string
	fullmektig: string
	fullmektigsNavn: string
	registrert: string
	opphoert?: boolean
	kilde: string
	omraade: Omraade[]
}

export type Omraade = {
	tema: string
	handling: string[]
}

export const FullmaktHandling = {
	les: ['LES', 'KOMMUNISER'],
	lesOgSkriv: ['LES', 'KOMMUNISER', 'SKRIV'],
}
