import { Kategorier, SubKategorier } from '../Categories'
import { Attributt, InputType, DataSource, AttributtType } from '../Types'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'
import DateValidation from '~/components/fields/Datepicker/DateValidation'

import * as yup from 'yup'

const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.KontaktInfo,
		subKategori: SubKategorier.Krr,
		id: 'rrgr',
		label: 'Har kontakt og reservasjon',
		dataSource: DataSource.KRR,
		attributtType: AttributtType.SelectAndEdit,
		validation: yup.object(),
		items: [
			{
				hovedKategori: Kategorier.KontaktInfo,
				subKategori: SubKategorier.Krr,
				id: 'mobil',
				label: 'Mobilnummer',
				dataSource: DataSource.KRR,
				inputType: InputType.Text,
				validation: yup.string().required('Vennligst oppgi mobilnummer'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.KontaktInfo,
				subKategori: SubKategorier.Krr,
				id: 'epost',
				label: 'E-postadresse',
				dataSource: DataSource.KRR,
				inputType: InputType.Text,
				validation: yup.string().email('Vennligst fyll ut'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.KontaktInfo,
				subKategori: SubKategorier.Krr,
				id: 'reservert',
				dataSource: DataSource.KRR,
				label: 'Reservert mot digitalkommunikasjon',
				inputType: InputType.Select,
				options: SelectOptionsManager('boolean'),
				validation: yup.string().required('Vennligst velg en verdi'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.KontaktInfo,
				subKategori: SubKategorier.Krr,
				id: 'gyldigFra',
				label: 'Gyldig fra',
				dataSource: DataSource.KRR,
				validation: DateValidation(false),
				inputType: InputType.Date,
				attributtType: AttributtType.SelectAndEdit
			}
		]
	}
]

export default AttributtListe
