import { Kategorier, SubKategorier } from '../Categories'
import { Attributt, InputType, DataSource, AttributtType } from '../Types'
import Formatters from '~/utils/DataFormatter'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'
import DateValidation from '~/components/fields/Datepicker/DateValidation'
import { NumberValidator } from '~/utils/Validator'
import * as yup from 'yup'

const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.ArbeidOgInntekt,
		subKategori: SubKategorier.Arbeidsforhold,
		id: 'arbeidsforhold',
		label: 'Har arbeidsforhold',
		dataSource: DataSource.AAREG,
		attributtType: AttributtType.SelectAndEdit,
		validation: yup.object(),
		items: [
			{
				hovedKategori: Kategorier.ArbeidOgInntekt,
				subKategori: SubKategorier.Arbeidsforhold,
				id: 'yrke',
				label: 'Yrke',
				path: 'arbeidsavtale',
				dataSource: DataSource.AAREG,
				inputType: InputType.Select,
				options: SelectOptionsManager('yrke'),
				validation: yup.string().required('Velg et yrke.'),
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
				validation: DateValidation,
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.ArbeidOgInntekt,
				subKategori: SubKategorier.Arbeidsforhold,
				id: 'tom',
				label: 'Sluttdato',
				path: 'ansettelsesPeriode',
				dataSource: DataSource.AAREG,
				validation: DateValidation,
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
				inputTypeAttributes: {
					min: 0
				},
				validation: yup
					.number()
					.min(1, 'Stillingprosent kan ikke være mindre enn 1')
					.max(100, 'Stillingen prosent kan ikke være større enn 100')
					.required('Tast inn en gyldig stillingprosent'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.ArbeidOgInntekt,
				subKategori: SubKategorier.Arbeidsforhold,
				id: 'aktoertype',
				label: 'Type av arbeidsgiver',
				path: 'arbeidsgiver',
				dataSource: DataSource.AAREG,
				inputType: InputType.Select,
				options: SelectOptionsManager('aktoertype'),
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
				inputType: InputType.Number,
				inputTypeAttributes: {
					min: 0
				},
				// Egen validation pga yup tror stor streng ikke er integer
				validation: NumberValidator.required('Tast inn et gyldig orgnummer/ident')
					.min(1, 'Tast inn et gyldig orgnummer/ident')
					.max(99999999999, 'Ugyldig input'),
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
	}
]

export default AttributtListe
