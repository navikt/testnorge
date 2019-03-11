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
				hovedKategori: Kategorier.Inntekt,
				subKategori: SubKategorier.Inntekt,
				id: 'yrke',
				label: 'Yrke',
				path: 'ikkesett',
				editPath: 'tjeneste',
				dataSource: DataSource.SIGRUN,
				inputType: InputType.Text,
				validation: yup.string().required('Velg en type tjeneste.'),
				attributtType: AttributtType.SelectAndRead
			}
		]
	}
]

export default AttributtListe
