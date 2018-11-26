import { Kategorier, SubKategorier } from '../Categories'
import { Attributt, InputType, DataSource, AttributtType } from '../Types'
import Formatters from '~/utils/DataFormatter'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'
import DateValidation from '~/components/fields/Datepicker/DateValidation'

import * as yup from 'yup'

const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Alder,
		id: 'foedtEtter',
		label: 'Født etter',
		dataSource: DataSource.TPSF,
		inputType: InputType.Date,
		validation: DateValidation,
		attributtType: AttributtType.SelectOnly
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Alder,
		id: 'foedtFoer',
		label: 'Født før',
		dataSource: DataSource.TPSF,
		inputType: InputType.Date,
		validation: DateValidation,
		attributtType: AttributtType.SelectOnly
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Alder,
		id: 'doedsdato',
		label: 'Dødsdato',
		dataSource: DataSource.TPSF,
		inputType: InputType.Date,
		validation: DateValidation,
		attributtType: AttributtType.SelectOnly
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Nasjonalitet,
		id: 'statsborgerskap',
		label: 'Statsborgerskap',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		apiKodeverkId: 'StatsborgerskapFreg',
		validation: yup.string().required('Krever et statsborgerskap.'),
		kanRedigeres: true,
		attributtType: AttributtType.SelectAndEdit
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'kjonn',
		label: 'Kjønn',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		apiKodeverkId: 'Kj%C3%B8nnstyper',
		validation: yup.string().required('Velg kjønn.'),
		format: Formatters.kjonnToString,
		attributtType: AttributtType.SelectAndEdit
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'sivilstand',
		label: 'Sivilstander',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		apiKodeverkId: 'Sivilstander',
		validation: yup.string().required('Velg sivilstand.'),
		kanRedigeres: true,
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
		validation: yup.string().required('Velg diskresjonskoder.'),
		kanRedigeres: true,
		attributtType: AttributtType.SelectAndEdit
	}
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
