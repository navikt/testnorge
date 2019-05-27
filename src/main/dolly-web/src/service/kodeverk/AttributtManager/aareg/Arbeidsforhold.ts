import { Kategorier, SubKategorier, SubSubKategorier } from '../Categories'
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
		isMultiple: true,
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
				apiKodeverkId: 'Yrker',
				apiKodeverkShowValueInLabel: true,
				validation: yup.string().required('Velg et yrke.'),
				size: 'large',
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
				validation: DateValidation(),
				attributtType: AttributtType.SelectAndRead,
				defaultValue: new Date().setFullYear(new Date().getFullYear() - 20)
			},
			{
				hovedKategori: Kategorier.ArbeidOgInntekt,
				subKategori: SubKategorier.Arbeidsforhold,
				id: 'tom',
				label: 'Sluttdato',
				path: 'ansettelsesPeriode',
				dataSource: DataSource.AAREG,
				validation: DateValidation(false),
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
				attributtType: AttributtType.SelectAndRead,
				defaultValue: 100
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
				label: 'Orgnummer',
				path: 'arbeidsgiver',
				dataSource: DataSource.AAREG,
				inputType: InputType.Text,
				onlyShowAfterSelectedValue: { attributtId: 'aktoertype', valueIndex: 0 },
				// Egen validation pga yup tror stor streng ikke er integer

				validation: yup
					.string()
					.matches(/^[0-9]*$/, 'Orgnummer må være et tall med 9 sifre')
					.test('len', 'Orgnummer må være et tall med 9 sifre', val => val && val.length === 9),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.ArbeidOgInntekt,
				subKategori: SubKategorier.Arbeidsforhold,
				id: 'ident',
				label: 'Arbeidsgiver ident',
				path: 'arbeidsgiver',
				dataSource: DataSource.AAREG,
				inputType: InputType.Text,
				onlyShowAfterSelectedValue: { attributtId: 'aktoertype', valueIndex: 1 },
				// Egen validation pga yup tror stor streng ikke er integer
				validation: yup
					.string()
					.matches(/^[0-9]*$/, 'Ident må være et tall med 11 sifre')
					.test('len', 'Ident må være et tall med 11 sifre', val => val && val.length === 11),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.ArbeidOgInntekt,
				subKategori: SubKategorier.Arbeidsforhold,
				id: 'permisjon',
				label: 'Permisjon',
				path: 'permisjon',
				dataSource: DataSource.AAREG,
				isMultiple: true,
				attributtType: AttributtType.SelectAndRead,
				informasjonstekst:
					'Permisjonen må ha start- og sluttdato innenfor tidsrommet til arbeidsforholdet.',
				subItems: [
					{
						hovedKategori: Kategorier.ArbeidOgInntekt,
						subKategori: SubKategorier.Arbeidsforhold,
						subSubKategori: SubSubKategorier.Permisjon,
						id: 'permisjonOgPermittering',
						label: 'Type',
						path: 'type',
						size: 'medium',
						dataSource: DataSource.AAREG,
						validation: yup.string().required('Velg en type permisjon.'),
						inputType: InputType.Select,
						apiKodeverkId: 'PermisjonsOgPermitteringsBeskrivelse',
						attributtType: AttributtType.SelectAndRead
					},
					{
						hovedKategori: Kategorier.ArbeidOgInntekt,
						subKategori: SubKategorier.Arbeidsforhold,
						subSubKategori: SubSubKategorier.Permisjon,
						id: 'fom',
						label: 'Startdato',
						path: 'permisjonsperiode.startdato',
						validation: DateValidation(),
						dataSource: DataSource.AAREG,
						inputType: InputType.Date,
						attributtType: AttributtType.SelectAndRead
						//defaultValue: new Date().setFullYear(new Date().getFullYear() - 20)
					},
					{
						hovedKategori: Kategorier.ArbeidOgInntekt,
						subKategori: SubKategorier.Arbeidsforhold,
						subSubKategori: SubSubKategorier.Permisjon,
						id: 'tom',
						label: 'Sluttdato',
						path: 'permisjonsperiode.sluttdato',
						validation: DateValidation(),
						dataSource: DataSource.AAREG,
						inputType: InputType.Date,
						attributtType: AttributtType.SelectAndRead
					},
					{
						hovedKategori: Kategorier.ArbeidOgInntekt,
						subKategori: SubKategorier.Arbeidsforhold,
						subSubKategori: SubSubKategorier.Permisjon,
						id: 'permisjonsprosent',
						label: 'Permisjonsprosent',
						path: 'permisjonsprosent',
						dataSource: DataSource.AAREG,
						inputType: InputType.Number,
						size: 'small',
						inputTypeAttributes: {
							min: 0
						},
						validation: yup
							.number()
							.min(1, 'Stillingprosent kan ikke være mindre enn 1')
							.max(100, 'Stillingen prosent kan ikke være større enn 100')
							.required('Tast inn en gyldig stillingprosent'),
						attributtType: AttributtType.SelectAndRead,
						defaultValue: 100
					}
				]
			},
			{
				hovedKategori: Kategorier.ArbeidOgInntekt,
				subKategori: SubKategorier.Arbeidsforhold,
				id: 'utenlandsopphold',
				label: 'Utenlandsopphold',
				path: 'utenlandsopphold',
				dataSource: DataSource.AAREG,
				attributtType: AttributtType.SelectAndRead,
				informasjonstekst:
					'Utenlandsoppholdet må ha start- og sluttdato innenfor samme kalendermåned i tidsrommet til arbeidsforholdet.',
				subItems: [
					{
						hovedKategori: Kategorier.ArbeidOgInntekt,
						subKategori: SubKategorier.Arbeidsforhold,
						subSubKategori: SubSubKategorier.Utenlandsopphold,
						id: 'land',
						label: 'Land',
						path: 'utenlandsopphold.land',
						dataSource: DataSource.AAREG,
						inputType: InputType.Select,
						validation: yup.string().required('Velg et land.'),
						apiKodeverkId: 'LandkoderISO2',
						attributtType: AttributtType.SelectAndRead
					},
					{
						hovedKategori: Kategorier.ArbeidOgInntekt,
						subKategori: SubKategorier.Arbeidsforhold,
						subSubKategori: SubSubKategorier.Utenlandsopphold,
						id: 'fom',
						label: 'Startdato',
						path: 'utenlandsopphold.startdato',
						validation: DateValidation(),
						dataSource: DataSource.AAREG,
						inputType: InputType.Date,
						attributtType: AttributtType.SelectAndRead,
						defaultValue: new Date().setFullYear(new Date().getFullYear())
					},
					{
						hovedKategori: Kategorier.ArbeidOgInntekt,
						subKategori: SubKategorier.Arbeidsforhold,
						subSubKategori: SubSubKategorier.Utenlandsopphold,
						id: 'tom',
						label: 'Sluttdato',
						path: 'utenlandsopphold.sluttdato',
						//validation: DateValidation(false),
						dataSource: DataSource.AAREG,
						inputType: InputType.Date,
						attributtType: AttributtType.SelectAndRead
					}
				]
			}
		]
	}
]

export default AttributtListe
