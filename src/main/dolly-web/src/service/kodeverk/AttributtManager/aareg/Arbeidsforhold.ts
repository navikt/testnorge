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
		dataSource: DataSource.SIGRUN,
		validation: yup.object(),
		attributtType: AttributtType.SelectAndEdit,
		items: [
			{
				hovedKategori: Kategorier.ArbeidOgInntekt,
				subKategori: SubKategorier.Arbeidsforhold,
				id: 'yrke',
				label: 'Yrke',
				path: 'ikkesett',
				dataSource: DataSource.SIGRUN,
				inputType: InputType.Text,
				validation: yup.string().required('Velg en type tjeneste.'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.ArbeidOgInntekt,
				subKategori: SubKategorier.Arbeidsforhold,
				id: 'arbeidsforholdFra',
				label: 'Startdato',
				path: 'ikkesett',
				dataSource: DataSource.SIGRUN,
				inputType: InputType.Date,
				validation: yup.string().required('Velg en type tjeneste.'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.ArbeidOgInntekt,
				subKategori: SubKategorier.Arbeidsforhold,
				id: 'arbeidsforholdTil',
				label: 'Sluttdato',
				path: 'ikkesett',
				dataSource: DataSource.SIGRUN,
				inputType: InputType.Date,
				validation: yup.string().required('Velg en type tjeneste.'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.ArbeidOgInntekt,
				subKategori: SubKategorier.Arbeidsforhold,
				id: 'yrk2e',
				label: 'Stilling',
				path: 'ikkesett',
				dataSource: DataSource.SIGRUN,
				inputType: InputType.Text,
				validation: yup.string().required('Velg en type tjeneste.'),
				attributtType: AttributtType.SelectAndRead
			}
		],
		subItems: [
			{
				id: 'permisjon',
				label: 'Permisjon',
				items: [
					{
						hovedKategori: Kategorier.ArbeidOgInntekt,
						subKategori: SubKategorier.Arbeidsforhold,
						id: 'type',
						label: 'Type',
						path: 'ikkesett',
						dataSource: DataSource.SIGRUN,
						inputType: InputType.Text,
						attributtType: AttributtType.SelectAndRead
					}
				]
			},
			{
				id: 'Utenlandsopphold',
				label: 'Utenlandsopphold',
				items: [
					{
						hovedKategori: Kategorier.ArbeidOgInntekt,
						subKategori: SubKategorier.Arbeidsforhold,
						id: 'type222',
						label: 'Type',
						path: 'ikkesett',
						dataSource: DataSource.SIGRUN,
						inputType: InputType.Text,
						attributtType: AttributtType.SelectAndRead
					}
				]
			}
		]

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
		// 					dataSource: DataSource.SIGRUN,
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
