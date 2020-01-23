import { Kategorier, SubKategorier } from '../Categories'
import { Attributt, InputType, DataSource, AttributtType } from '../Types'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'

import * as yup from 'yup'

const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.ArbeidOgInntekt,
		subKategori: SubKategorier.Inntekt,
		id: 'inntekt',
		label: 'Har inntekt',
		dataSource: DataSource.SIGRUN,
		validation: yup.object(),
		isMultiple: true,
		attributtType: AttributtType.SelectAndEdit,
		items: [
			{
				hovedKategori: Kategorier.Inntekt,
				subKategori: SubKategorier.Inntekt,
				id: 'inntektsaar',
				label: 'År',
				path: 'inntektsaar',
				dataSource: DataSource.SIGRUN,
				inputType: InputType.Number,
				inputTypeAttributes: {
					min: 0
				},
				validation: yup
					.number()
					.integer('Ugyldig årstall')
					.required('Tast inn et gyldig år')
					.min(1968, 'Inntektsår må være 1968 eller senere')
					.max(2100, 'Inntektsår må være tidligere enn 2100'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Inntekt,
				subKategori: SubKategorier.Inntekt,
				id: 'inntektssted',
				label: 'inntektssted',
				path: 'inntektssted',
				editPath: 'inntektssted',
				defaultValue: 'Fastlandet',
				dataSource: DataSource.SIGRUN,
				inputType: InputType.Select,
				options: SelectOptionsManager('inntektssted'),
				validation: yup.string().required('Velg et grunnlag.'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Inntekt,
				subKategori: SubKategorier.Inntekt,
				id: 'tjeneste',
				label: 'Tjeneste',
				path: 'tekniskNavn',
				editPath: 'tjeneste',
				dataSource: DataSource.SIGRUN,
				inputType: InputType.Select,
				options: SelectOptionsManager('tjeneste'),
				/*onlyShowDependentOnOtherValue: {
					attributtId: 'inntektssted',
					value: ['Fastlandet']
				},*/
				validation: yup.string().required('Velg en type tjeneste.'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Inntekt,
				subKategori: SubKategorier.Inntekt,
				id: 'typeinntekt',
				label: 'Type inntekt',
				path: 'tekniskNavn',
				editPath: 'grunnlag',
				dataSource: DataSource.SIGRUN,
				inputType: InputType.Select,
				size: 'xlarge',
				hoydeOptions: 'medium',
				dependentOn: 'tjeneste',
				//validation: yup.string().required('Velg en type inntekt.'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Inntekt,
				subKategori: SubKategorier.Inntekt,
				path: 'verdi',
				id: 'beloep',
				label: 'Beløp',
				dataSource: DataSource.SIGRUN,
				inputType: InputType.Number,
				inputTypeAttributes: {
					min: 0
				},
				validation: yup
					.number()
					.min(0, 'Tast inn et gyldig beløp')
					.required('Oppgi beløpet'),
				attributtType: AttributtType.SelectAndEdit
			}
		]
	}
]

export default AttributtListe
