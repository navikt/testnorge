import { Kategorier, SubKategorier } from '../Categories'
import { Attributt, InputType, DataSource } from '../Types'
import Formatters from '~/utils/DataFormatter'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'
import DateValidation from '~/components/fields/Datepicker/DateValidation'

import * as yup from 'yup'

const AttributtListe: Attributt[] = [
	{
		// TODO: Bytt til radio buttons
		hovedKategori: Kategorier.Inntekter,
		subKategori: SubKategorier.Inntekt,
		id: 'inntekter',
		path: 'inntekter.inntekt',
		label: 'Register inntekt',
		dataSource: DataSource.SIGRUN,
		validation: yup.object(),
		items: [
			{
				hovedKategori: Kategorier.Inntekt,
				subKategori: SubKategorier.Inntekt,
				id: 'typeinntekt',
				label: 'Type inntekt',
				dataSource: DataSource.SIGRUN,
				inputType: InputType.Select,
				options: [{ label: 'test', value: 'test' }],
				validation: yup.string().required('Velg en type inntekt.')
			},
			{
				hovedKategori: Kategorier.Inntekt,
				subKategori: SubKategorier.Inntekt,
				id: 'beløp',
				label: 'Beløp',
				dataSource: DataSource.SIGRUN,
				inputType: InputType.Number,
				validation: yup.number().required('Oppgi en sum.')
			},
			{
				hovedKategori: Kategorier.Inntekt,
				subKategori: SubKategorier.Inntekt,
				id: 'År',
				label: 'År',
				dataSource: DataSource.SIGRUN,
				inputType: InputType.Date,
				validation: DateValidation
			}
		]
	}
	// {
	// 	hovedKategori: Kategorier.Inntekter,
	// 	subKategori: SubKategorier.Inntekt,
	// 	id: 'inntekter',
	// 	path: 'inntekter.inntekt',
	// 	label: 'Register beregnet skattegrunnlag',
	// 	dataSource: DataSource.TPSF,
	// 	validation: yup.object()
	// },
	// {
	// 	hovedKategori: Kategorier.Inntekter,
	// 	subKategori: SubKategorier.Inntekt,
	// 	id: 'inntekter',
	// 	path: 'inntekter.inntekt',
	// 	label: 'Register summert skattegrunnlag',
	// 	dataSource: DataSource.TPSF,
	// 	validation: yup.object()
	// }
]

export default AttributtListe
