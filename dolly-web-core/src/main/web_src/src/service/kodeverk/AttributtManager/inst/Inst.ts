import { Kategorier, SubKategorier } from '../Categories'
import { Attributt, InputType, DataSource, AttributtType } from '../Types'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'
import DateValidation from '~/components/fields/Datepicker/DateValidation'

import * as yup from 'yup'

const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Institusjonsopphold,
		id: 'institusjonsopphold',
		label: 'Har institusjonsopphold',
		path: 'institusjonsopphold',
		dataSource: DataSource.INST,
		validation: yup.object(),
		isMultiple: true,
		attributtType: AttributtType.SelectAndEdit,
		items: [
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Institusjonsopphold,
				id: 'institusjonstype',
				label: 'Institusjonstype',
				path: 'institusjonstype',
				dataSource: DataSource.INST,
				inputType: InputType.Select,
				options: SelectOptionsManager('institusjonstype'),
				validation: yup.string().required('Velg en type institusjon.'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Institusjonsopphold,
				id: 'startdato',
				label: 'Startdato',
				path: 'startdato',
				dataSource: DataSource.INST,
				inputType: InputType.Date,
				validation: DateValidation(),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Institusjonsopphold,
				id: 'faktiskSluttdato',
				label: 'Sluttdato',
				path: 'faktiskSluttdato',
				dataSource: DataSource.INST,
				inputType: InputType.Date,
				validation: DateValidation(false),
				attributtType: AttributtType.SelectAndEdit
			}
		]
	}
]

export default AttributtListe
