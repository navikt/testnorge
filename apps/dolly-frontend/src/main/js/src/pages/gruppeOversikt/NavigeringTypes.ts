export enum SoekTypeValg {
	PERSON = 'Person',
	BESTILLING = 'Bestilling',
	GRUPPE = 'Gruppe',
}

export interface NavigeringOption {
	value: string
	label: string
	type: SoekTypeValg
}

export interface GroupedOption {
	label: string
	options: NavigeringOption[]
}
