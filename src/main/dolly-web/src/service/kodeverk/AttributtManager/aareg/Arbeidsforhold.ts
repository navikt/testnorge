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
		attributtType: AttributtType.SelectAndEdit,
		items: [
			{
				hovedKategori: Kategorier.ArbeidOgInntekt,
				subKategori: SubKategorier.Arbeidsforhold,
				id: 'yrke',
				label: 'Yrke',
				path: 'arbeidsavtale',
				dataSource: DataSource.AAREG,
				inputType: InputType.Text,
				// apiKodeverkId: 'Yrker',
				validation: yup.string().required('Velg en type tjeneste.'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.ArbeidOgInntekt,
				subKategori: SubKategorier.Arbeidsforhold,
				id: 'fom',
				label: 'Startdato',
				path: 'ansettelsesPeriode',
				dataSource: DataSource.AAREG,
				inputType: InputType.Date,
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.ArbeidOgInntekt,
				subKategori: SubKategorier.Arbeidsforhold,
				id: 'tom',
				label: 'Sluttdato',
				path: 'ansettelsesPeriode',
				dataSource: DataSource.AAREG,
				inputType: InputType.Date,
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.ArbeidOgInntekt,
				subKategori: SubKategorier.Arbeidsforhold,
				id: 'stillingsprosent',
				label: 'Stillingprosent',
				path: 'arbeidsavtale',
				dataSource: DataSource.AAREG,
				inputType: InputType.Number,
				validation: yup.string().required('Krever stillingprosent'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.ArbeidOgInntekt,
				subKategori: SubKategorier.Arbeidsforhold,
				id: 'aktoertype',
				label: 'Type av arbeidsgiver',
				path: 'arbeidsgiver',
				dataSource: DataSource.AAREG,
				inputType: InputType.Text,
				validation: yup.string().required('Velg en type av arbeidsgiver'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.ArbeidOgInntekt,
				subKategori: SubKategorier.Arbeidsforhold,
				id: 'orgnummer',
				label: 'Orgnummer/ident',
				path: 'arbeidsgiver',
				dataSource: DataSource.AAREG,
				inputType: InputType.Text,
				validation: yup.string().required('Krever orgnummer/ident'),
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
