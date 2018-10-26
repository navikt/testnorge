import { Kategorier, SubKategorier } from '../Categories'
import { Attributt, InputType, DataSource } from '../Types'
import Formatters from '~/utils/DataFormatter'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'
import DateValidation from '~/components/fields/Datepicker/DateValidation'

import * as yup from 'yup'

const AttributtListe: Attributt[] = [
	// {
	// 	hovedKategori: Kategorier.Inntekter,
	// 	subKategori: SubKategorier.Inntekt,
	// 	id: 'inntekter',
	// 	path: 'inntekter.inntekt',
	// 	label: 'Kun inntektsbeløp',
	// 	dataSource: DataSource.SIGRUN,
	// 	validation: yup.object(),
	// 	items: [
	// 		{
	// 			hovedKategori: Kategorier.Inntekt,
	// 			subKategori: SubKategorier.Inntekt,
	// 			id: 'beløp',
	// 			label: 'Beløp',
	// 			path: 'inntekt.belop',

	// 			dataSource: DataSource.SIGRUN,
	// 			inputType: InputType.Number,
	// 			validation: yup.number().required('Oppgi en sum.')
	// 		},
	// 		{
	// 			hovedKategori: Kategorier.Inntekt,
	// 			subKategori: SubKategorier.Inntekt,
	// 			path: 'inntekt.aar',
	// 			id: 'År',
	// 			label: 'År',
	// 			dataSource: DataSource.SIGRUN,
	// 			inputType: InputType.Date,
	// 			validation: DateValidation
	// 		}
	// 	]
	// },
	{
		hovedKategori: Kategorier.Inntekter,
		subKategori: SubKategorier.Inntekt,
		id: 'beregnetskattgrunnlag',
		label: 'Beregnet skattegrunnlag',
		dataSource: DataSource.SIGRUN,
		validation: yup.object(),
		items: [
			{
				hovedKategori: Kategorier.Inntekt,
				subKategori: SubKategorier.Inntekt,
				id: 'typeinntekt',
				label: 'Type inntekt',
				path: 'grunnlag.tekniskNavn',
				dataSource: DataSource.SIGRUN,
				inputType: InputType.Select,
				apiKodeverkId: 'Beregnet skatt',
				// options: [{ label: 'test', value: 'test' }],
				validation: yup.string().required('Velg en type inntekt.')
			},
			{
				hovedKategori: Kategorier.Inntekt,
				subKategori: SubKategorier.Inntekt,
				path: 'grunnlag.verdi',
				id: 'beloep',
				label: 'Beløp',
				dataSource: DataSource.SIGRUN,
				inputType: InputType.Number,
				validation: yup.number().required('Oppgi en sum.')
			},
			{
				hovedKategori: Kategorier.Inntekt,
				subKategori: SubKategorier.Inntekt,
				id: 'aar',
				label: 'År',
				path: 'inntektsaar',
				dataSource: DataSource.SIGRUN,
				inputType: InputType.Date,
				validation: DateValidation
			}
		]
	},
	{
		hovedKategori: Kategorier.Inntekter,
		subKategori: SubKategorier.Inntekt,
		id: 'summertskattegrunnlag',
		path: 'inntekter.inntekt',
		label: 'Summert skattegrunnlag',
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
				apiKodeverkId: 'Summert skattegrunnlag',
				// options: [{ label: 'test', value: 'test' }],
				validation: yup.string().required('Velg en type inntekt.')
			},
			{
				hovedKategori: Kategorier.Inntekt,
				subKategori: SubKategorier.Inntekt,
				id: 'beløp',
				label: 'Beløp',
				dataSource: DataSource.SIGRUN,
				inputType: InputType.Number,
				validation: yup
					.number()
					.positive('Tast inn et gyldig beløp')
					.required('Oppgi beløpet')
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
]

export default AttributtListe
