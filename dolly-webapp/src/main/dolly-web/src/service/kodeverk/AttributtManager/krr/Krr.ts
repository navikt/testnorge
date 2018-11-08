import { Kategorier, SubKategorier } from '../Categories'
import { Attributt, InputType, DataSource } from '../Types'
import Formatters from '~/utils/DataFormatter'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'
import DateValidation from '~/components/fields/Datepicker/DateValidation'

import * as yup from 'yup'

const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.KontaktInfo,
		subKategori: SubKategorier.Krr,
		id: 'mobil',
		label: 'Mobilnummer',
		dataSource: DataSource.SIGRUN,
		validation: yup.object(),
		items: [
			{
				hovedKategori: Kategorier.Inntekt,
				subKategori: SubKategorier.Inntekt,
				id: 'mobil',
				label: 'Mobilnummer',
				path: 'mobilnummer',
				dataSource: DataSource.SIGRUN,
				inputType: InputType.Text,
				validation: yup.string().required('Velg en type tjeneste.')
			}
		]
	},
	{
		hovedKategori: Kategorier.KontaktInfo,
		subKategori: SubKategorier.Krr,
		id: 'epost',
		label: 'E-postadresse',
		dataSource: DataSource.SIGRUN,
		validation: yup.object(),
		items: [
			{
				hovedKategori: Kategorier.Inntekt,
				subKategori: SubKategorier.Inntekt,
				id: 'epost',
				label: 'E-postadresse',
				path: 'epost',
				dataSource: DataSource.SIGRUN,
				inputType: InputType.Text,
				validation: yup.string().required('Velg en type tjeneste.')
			}
		]
	},
	{
		hovedKategori: Kategorier.KontaktInfo,
		subKategori: SubKategorier.Krr,
		id: 'reservert',
		label: 'Reservert mot digitalkommmunikasjon',
		dataSource: DataSource.SIGRUN,
		validation: yup.object(),
		items: [
			{
				hovedKategori: Kategorier.Inntekt,
				subKategori: SubKategorier.Inntekt,
				id: 'tjeneste',
				label: 'Tjeneste',
				path: 'tekniskNavn',
				dataSource: DataSource.SIGRUN,
				inputType: InputType.Text,
				options: ['JA', 'NEI'],
				validation: yup.string().required('Velg en type tjeneste.')
			}
		]
	}
]

export default AttributtListe
