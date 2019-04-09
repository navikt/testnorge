import { Kategorier, SubKategorier } from '../Categories'
import { Attributt, InputType, DataSource, AttributtType } from '../Types'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'
import DateValidation from '~/components/fields/Datepicker/DateValidation'

const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Alder,
		id: 'foedtEtter',
		label: 'Født etter',
		dataSource: DataSource.TPSF,
		inputType: InputType.Date,
		validation: DateValidation(),
		attributtType: AttributtType.SelectOnly,
		sattForEksisterendeIdent: true,
		defaultValue: new Date().setFullYear(new Date().getFullYear() - 80)
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Alder,
		id: 'foedtFoer',
		label: 'Født før',
		dataSource: DataSource.TPSF,
		inputType: InputType.Date,
		validation: DateValidation(),
		attributtType: AttributtType.SelectOnly,
		sattForEksisterendeIdent: true,
		defaultValue: new Date()
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Alder,
		id: 'doedsdato',
		label: 'Dødsdato',
		dataSource: DataSource.TPSF,
		inputType: InputType.Date,
		validation: DateValidation(),
		attributtType: AttributtType.SelectOnly,
		defaultValue: new Date()
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Nasjonalitet,
		id: 'statsborgerskap',
		label: 'Statsborgerskap',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		apiKodeverkId: 'StatsborgerskapFreg',
		attributtType: AttributtType.SelectAndEdit
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'kjonn',
		label: 'Kjønn',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		apiKodeverkId: 'Kjønnstyper',
		attributtType: AttributtType.SelectAndEdit,
		sattForEksisterendeIdent: true
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'sivilstand',
		label: 'Sivilstander',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		apiKodeverkId: 'Sivilstander',
		attributtType: AttributtType.SelectAndEdit
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'spesreg',
		label: 'Diskresjonskoder',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		apiKodeverkId: 'Diskresjonskoder',
		attributtType: AttributtType.SelectAndEdit
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'sprakKode',
		label: 'Språk',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		apiKodeverkId: 'Språk',
		attributtType: AttributtType.SelectAndEdit
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'egenAnsattDatoFom',
		label: 'Egenansatt',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		options: SelectOptionsManager('boolean'),
		attributtType: AttributtType.SelectAndEdit,
		transform: egenAnsatt => (egenAnsatt ? new Date() : null)
	}

	// TODO: Skal vi få tilbake alder?
	// {
	// 	hovedKategori: Kategorier.PersInfo,
	// 	subKategori: SubKategorier.Alder,
	// 	id: 'alder',
	// 	label: 'Alder',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Number,
	// 	validation: yup
	// 		.number()
	// 		.typeError('Må være et tall')
	// 		.integer()
	// 		.positive()
	// 		.max(125)
	// },
	// {
	// 	hovedKategori: Kategorier.PersInfo,
	// 	subKategori: SubKategorier.Alder,
	// 	id: 'fraAlder',
	// 	label: 'Fra alder',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Date,
	// 	validation: yup.date()
	// },
	// {
	// 	hovedKategori: Kategorier.PersInfo,
	// 	subKategori: SubKategorier.Alder,
	// 	id: 'tilAlder',
	// 	label: 'Til alder',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Date,
	// 	validation: yup.date()
	// }
]

export default AttributtListe
