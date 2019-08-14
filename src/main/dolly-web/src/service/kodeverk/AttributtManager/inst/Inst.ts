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
		genererSyntVerdier: true,
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
				id: 'varighet',
				label: 'Varighet',
				path: 'varighet',
				dataSource: DataSource.INST,
				inputType: InputType.Select,
				options: SelectOptionsManager('varighet'),
				validation: yup.string().required('Velg en type varighet.'),
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
				id: 'sluttdato',
				label: 'Sluttdato',
				path: 'sluttdato',
				dataSource: DataSource.INST,
				inputType: InputType.Date,
				validation: DateValidation(false),
				attributtType: AttributtType.SelectAndEdit
			}
		]
	}
]

export default AttributtListe
