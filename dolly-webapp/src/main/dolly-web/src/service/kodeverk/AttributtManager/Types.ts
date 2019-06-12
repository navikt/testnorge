import * as yup from 'yup'

// Kategori
export enum InputType {
	Text = 'text',
	Number = 'number',
	Select = 'select',
	Date = 'date'
}

export enum DataSource {
	TPSF = 'TPSF',
	SIGRUN = 'SIGRUN',
	KRR = 'KRR',
	AAREG = 'AAREG',
	PDLF = 'PDLF',
	ARENA = 'ARENA'
}

export interface Options {
	label: string
	value: string | number
}

export interface Kategori {
	id: string
	navn: string
	order: number
	informasjonstekst?: string
	showInSummary?: boolean
}

export interface KategoriTypes {
	[key: string]: Kategori
}

export enum AttributtType {
	SelectAndEdit = 'selectAndEdit', // kan bestilles og redigeres på
	EditOnly = 'editOnly', // kun redigering
	SelectOnly = 'selectOnly', // kun bestilling
	SelectAndRead = 'selectAndRead' // skal bestilles, ikke editeres, men vises i editmode
}

export interface Attributt {
	hovedKategori: Kategori
	size?: String
	subKategori: Kategori
	subSubKategori?: Kategori
	id: string
	path?: string
	editPath?: string
	attributtType: AttributtType
	label: string
	dataSource: DataSource
	inputType?: InputType
	inputTypeAttributes?: object
	options?: Options[]
	format?: Function
	defaultValue?: any
	apiKodeverkId?: string
	apiKodeverkShowValueInLabel?: boolean
	validation?: yup.MixedSchema
	parent?: string
	items?: Attributt[]
	isMultiple?: boolean
	subItems?: Attributt[]
	dependentOn?: string // Er avhengig av en annen attributt for å kunne settes verdi på
	dependentBy?: string // Er ikke avhengig, er attributten som ble settet av dependentOn
	includeIf?: Attributt[]
	sattForEksisterendeIdent?: boolean
	onlyShowAfterSelectedValue?: Object // AAREG: Orgnummer/ident er avhengig av valgt verdi av "Type av arbeidsgiver". String = id, number = indexOf valgt verdi
	onlyShowDependentOnOtherValue?: Object //Dødsbo: Vise postnummer kodeverk hvis land=NOR. {attributtId: , dependentValue: [], except: []}
	transform?: (value: any, attributter: Attributt[]) => any
	informasjonstekst?: String
	subGruppe?: String
}

// Attributt grupper
export interface AttributtGruppe {
	hovedKategori: Kategori
	items: SubAttributtGruppe[]
}

export interface SubAttributtGruppe {
	subKategori: Kategori
	items: Attributt[]
}

export interface AttributtGruppeHovedKategori {
	hovedKategori: Kategori
	items: Attributt[]
}
