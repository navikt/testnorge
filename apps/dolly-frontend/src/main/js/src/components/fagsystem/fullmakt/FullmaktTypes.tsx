export type FullmaktTypes = {
	gyldigFraOgMed: string
	gyldigTilOgMed?: string
	fullmaktsgiver: string
	omraade: Omraade[]
}

export type Omraade = {
	tema: string
	handling: string[]
}
