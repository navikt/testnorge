enum HovedKategoriType {
	PersInfo = 'Personlig informasjon'
}

enum SubKategoriType {
	Alder = 'Alder',
	Nasjonalitet = 'Nasjonalitet'
}

enum InputType {
	Text = 'text',
	Number = 'number',
	Select = 'select',
	Date = 'date'
}

interface Options {
	label: String
	value: String | Number
}

interface Attributt {
	hovedKategori: HovedKategoriType
	subKategori: SubKategoriType
	id: String
	label: String
	type: InputType
	options?: Options
}

const AttributtKodeverk: Attributt[] = [
	{
		hovedKategori: HovedKategoriType.PersInfo,
		subKategori: SubKategoriType.Alder,
		id: 'foedtEtter',
		label: 'Født etter',
		type: InputType.Date
	},
	{
		hovedKategori: HovedKategoriType.PersInfo,
		subKategori: SubKategoriType.Alder,
		id: 'foedtFoer',
		label: 'Født før',
		type: InputType.Date
	},
	{
		hovedKategori: HovedKategoriType.PersInfo,
		subKategori: SubKategoriType.Nasjonalitet,
		id: 'statsborgerskap',
		label: 'Statsborgerskap',
		type: InputType.Select
	}
]

export default class AttributtContainer {
	private kodeverk

	constructor() {
		this.kodeverk = AttributtKodeverk
	}
}
