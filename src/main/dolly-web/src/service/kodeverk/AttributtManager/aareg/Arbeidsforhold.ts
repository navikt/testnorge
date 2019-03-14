import { Kategorier, SubKategorier } from '../Categories'
import { Attributt, InputType, DataSource, AttributtType } from '../Types'
import Formatters from '~/utils/DataFormatter'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'
import DateValidation from '~/components/fields/Datepicker/DateValidation'

import * as yup from 'yup'

const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.ArbeidOgInntekt,
		subKategori: SubKategorier.Arbeidsforhold,
		id: 'arbeidsforhold',
		label: 'Har arbeidsforhold',
		dataSource: DataSource.AAREG,
		validation: yup.object(),
		attributtType: AttributtType.SelectAndEdit,
		items: [
			{
				hovedKategori: Kategorier.ArbeidOgInntekt,
				subKategori: SubKategorier.Arbeidsforhold,
				id: 'yrke',
				label: 'Yrke',
				path: 'yrke',
				dataSource: DataSource.AAREG,
				inputType: InputType.Text,
				validation: yup.string().required('Velg en type tjeneste.'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.ArbeidOgInntekt,
				subKategori: SubKategorier.Arbeidsforhold,
				id: 'arbeidsforholdFra',
				label: 'Startdato',
				path: 'ansettelsesPeriode.fom',
				dataSource: DataSource.AAREG,
				inputType: InputType.Date,
				validation: yup.string().required('Velg en type tjeneste.'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.ArbeidOgInntekt,
				subKategori: SubKategorier.Arbeidsforhold,
				id: 'arbeidsforholdTil',
				label: 'Sluttdato',
				path: 'ansettelsesPeriode.tom',
				dataSource: DataSource.AAREG,
				inputType: InputType.Date,
				validation: yup.string().required('Velg en type tjeneste.'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.ArbeidOgInntekt,
				subKategori: SubKategorier.Arbeidsforhold,
				id: 'stilling',
				label: 'Stillingprosent',
				path: 'stillingprosent',
				dataSource: DataSource.AAREG,
				inputType: InputType.Text,
				validation: yup.string().required('Velg en type tjeneste.'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.ArbeidOgInntekt,
				subKategori: SubKategorier.Arbeidsforhold,
				id: 'arbeidsgiver',
				label: 'Arbeidsgiver',
				path: 'arbeidsgiver',
				dataSource: DataSource.AAREG,
				inputType: InputType.Text,
				validation: yup.string().required('Velg en type tjeneste.'),
				attributtType: AttributtType.SelectAndRead
			}
		]
		// subItems: [
		// 	{
		// 		id: 'permisjon',
		// 		label: 'Permisjon',
		// 		items: [
		// 			{
		// 				hovedKategori: Kategorier.ArbeidOgInntekt,
		// 				subKategori: SubKategorier.Arbeidsforhold,
		// 				id: 'type',
		// 				label: 'Type',
		// 				path: 'ikkesett',
		// 				dataSource: DataSource.AAREG,
		// 				inputType: InputType.Text,
		// 				attributtType: AttributtType.SelectAndRead
		// 			}
		// 		]
		// 	},
		// 	{
		// 		id: 'Utenlandsopphold',
		// 		label: 'Utenlandsopphold',
		// 		items: [
		// 			{
		// 				hovedKategori: Kategorier.ArbeidOgInntekt,
		// 				subKategori: SubKategorier.Arbeidsforhold,
		// 				id: 'type',
		// 				label: 'Type',
		// 				path: 'ikkesett',
		// 				dataSource: DataSource.AAREG,
		// 				inputType: InputType.Text,
		// 				attributtType: AttributtType.SelectAndRead
		// 			}
		// 		]
		// 	}
		// ]

		// subItems: [
		// 	[
		// 		{
		// 			id: 'bro',
		// 			label: 'string',
		// 			items: [
		// 				{
		// 					hovedKategori: Kategorier.Inntekt,
		// 					subKategori: SubKategorier.Arbeidsforhold,
		// 					id: 'yrke',
		// 					label: 'Stilling',
		// 					path: 'ikkesett',
		// 					dataSource: DataSource.AAREG,
		// 					inputType: InputType.Text,
		// 					validation: yup.string().required('Velg en type tjeneste.'),
		// 					attributtType: AttributtType.SelectAndRead
		// 				}
		// 			]
		// 		}
		// 	]
		// ]
	}
]

export default AttributtListe
