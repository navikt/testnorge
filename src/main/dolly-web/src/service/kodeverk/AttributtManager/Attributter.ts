import { Attributt, KategoriTypes, InputType, DataSource } from './Types'
import * as yup from 'yup'

const Kategorier: KategoriTypes = {
	PersInfo: {
		navn: 'Personinformasjon',
		order: 10
	},
	Inntekt: {
		navn: 'Inntekt',
		order: 20
	}
}

const SubKategorier: KategoriTypes = {
	Alder: {
		navn: 'Alder',
		order: 10
	},
	Nasjonalitet: {
		navn: 'Nasjonalitet',
		order: 20
	},
	Diverse: {
		navn: 'Diverse',
		order: 30
	}
}

const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Alder,
		id: 'foedtEtter',
		label: 'Født etter',
		dataSource: DataSource.TPSF,
		inputType: InputType.Date,
		validation: yup.date().required('Velg en dato')
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Alder,
		id: 'foedtFoer',
		label: 'Født før',
		dataSource: DataSource.TPSF,
		inputType: InputType.Date,
		validation: yup.date().required('Velg en dato')
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Nasjonalitet,
		id: 'statsborgerskap',
		label: 'Statsborgerskap',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		apiKodeverkId: 'statsborgerskap',
		validation: yup.string().required('Krever et statsborgerskap')
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'kjonn',
		label: 'Kjønn',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		options: [{ value: 'K', label: 'Kvinne' }, { value: 'M', label: 'Mann' }],
		validation: yup.string().required('Velg kjønn')
	},
	{
		hovedKategori: Kategorier.Inntekt,
		subKategori: SubKategorier.Diverse,
		id: 'inntekt',
		label: 'Inntektsmåned',
		dataSource: DataSource.SIGRUN,
		inputType: InputType.Text,
		validation: yup
			.string()
			.min(3, 'Må ha minst 3 tegn')
			.required('Sett inntektsmåned')
	}
]

export default AttributtListe
