import { Attributt, KategoriTypes, InputType, DataSource } from './Types'
import * as yup from 'yup'

const Kategorier: KategoriTypes = {
	PersInfo: {
		id: 'personinfo',
		navn: 'Personinformasjon',
		order: 10
	},
	Adresser: {
		id: 'adresser',
		navn: 'Adresser',
		order: 20
	}
}

const SubKategorier: KategoriTypes = {
	Alder: {
		id: 'alder',
		navn: 'Alder',
		order: 10
	},
	Nasjonalitet: {
		id: 'nasjonalitet',
		navn: 'Nasjonalitet',
		order: 20
	},
	Diverse: {
		id: 'diverse',
		navn: 'Diverse',
		order: 30
	},
	Boadresse: {
		id: 'boadresse',
		navn: 'Boadresse',
		order: 10,
		multiple: true
	},
	PostadrInnland: {
		id: 'postadresseInnland',
		navn: 'Postadresse innland',
		order: 20,
		multiple: true
	},
	PostadrUtland: {
		id: 'postadresseUtland',
		navn: 'Postadresse utland',
		order: 30,
		multiple: true
	}
}

const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Alder,
		id: 'alder',
		label: 'Alder',
		dataSource: DataSource.TPSF,
		inputType: InputType.Number,
		validation: yup
			.number()
			.integer()
			.positive()
			.max(125)
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Alder,
		id: 'fraAlder',
		label: 'Fra alder',
		dataSource: DataSource.TPSF,
		inputType: InputType.Date,
		validation: yup.date()
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Alder,
		id: 'tilAlder',
		label: 'Til alder',
		dataSource: DataSource.TPSF,
		inputType: InputType.Date,
		validation: yup.date()
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Alder,
		id: 'foedtEtter',
		label: 'Født etter',
		dataSource: DataSource.TPSF,
		inputType: InputType.Date,
		validation: yup.date()
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Alder,
		id: 'foedtFoer',
		label: 'Født før',
		dataSource: DataSource.TPSF,
		inputType: InputType.Date,
		validation: yup.date()
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Alder,
		id: 'dodsdato',
		label: 'Dødsdato',
		dataSource: DataSource.TPSF,
		inputType: InputType.Date,
		validation: yup.date()
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
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Boadresse,
		id: 'gatenavn',
		label: 'Gatenavn',
		dataSource: DataSource.TPSF,
		inputType: InputType.Text,
		validation: yup.string()
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Boadresse,
		id: 'husnummer',
		label: 'Husnummer',
		dataSource: DataSource.TPSF,
		inputType: InputType.Text,
		validation: yup.string()
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Boadresse,
		id: 'postnummer',
		label: 'Postnummer',
		dataSource: DataSource.TPSF,
		inputType: InputType.Text,
		validation: yup.string()
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Boadresse,
		id: 'geoTilknytning',
		label: 'Geografisk tilknytning',
		dataSource: DataSource.TPSF,
		inputType: InputType.Text,
		validation: yup.string()
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Boadresse,
		id: 'flyttedato',
		label: 'Flyttedato',
		dataSource: DataSource.TPSF,
		inputType: InputType.Date,
		validation: yup.date()
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Boadresse,
		id: 'matrikkeladresse',
		label: 'Matrikkeladresse',
		dataSource: DataSource.TPSF,
		inputType: InputType.Text,
		validation: yup.string()
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Boadresse,
		id: 'gardsnummer',
		label: 'Gårdsnummer',
		dataSource: DataSource.TPSF,
		inputType: InputType.Text,
		validation: yup.string()
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Boadresse,
		id: 'bruksnummer',
		label: 'Bruksnummer',
		dataSource: DataSource.TPSF,
		inputType: InputType.Text,
		validation: yup.string()
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.PostadrInnland,
		id: 'gatenavn',
		label: 'Gatenavn',
		dataSource: DataSource.TPSF,
		inputType: InputType.Text,
		validation: yup.string()
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.PostadrInnland,
		id: 'husnummer',
		label: 'Husnummer',
		dataSource: DataSource.TPSF,
		inputType: InputType.Text,
		validation: yup.string()
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.PostadrInnland,
		id: 'postnummer',
		label: 'Postnummer',
		dataSource: DataSource.TPSF,
		inputType: InputType.Text,
		validation: yup.string()
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.PostadrInnland,
		id: 'geoTilknytning',
		label: 'Geografisk tilknytning',
		dataSource: DataSource.TPSF,
		inputType: InputType.Text,
		validation: yup.string()
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.PostadrInnland,
		id: 'flyttedato',
		label: 'Flyttedato',
		dataSource: DataSource.TPSF,
		inputType: InputType.Date,
		validation: yup.date()
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.PostadrUtland,
		id: 'gatenavn',
		label: 'Gatenavn',
		dataSource: DataSource.TPSF,
		inputType: InputType.Text,
		validation: yup.string()
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.PostadrUtland,
		id: 'husnummer',
		label: 'Husnummer',
		dataSource: DataSource.TPSF,
		inputType: InputType.Text,
		validation: yup.string()
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.PostadrUtland,
		id: 'postnummer',
		label: 'Postnummer',
		dataSource: DataSource.TPSF,
		inputType: InputType.Text,
		validation: yup.string()
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.PostadrUtland,
		id: 'geoTilknytning',
		label: 'Geografisk tilknytning',
		dataSource: DataSource.TPSF,
		inputType: InputType.Text,
		validation: yup.string()
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.PostadrUtland,
		id: 'flyttedato',
		label: 'Flyttedato',
		dataSource: DataSource.TPSF,
		inputType: InputType.Date,
		validation: yup.date()
	}
]

export default AttributtListe
